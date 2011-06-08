/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.core.service;

import java.util.Arrays;
import java.util.Map.Entry;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class ProjectEnhancerService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectEnhancerService.class);

	public void enhanceWithBuildInformations(Project projectToMerge, ConnectionPlugin buildPlugin) {
		ProjectId projectId = projectToMerge.getProjectId();
		Preconditions.checkState(projectId != null, "projectToComplete must have a projectId");
		try {
			Project project = buildPlugin.findProject(projectId);
			if (project != null) {
				String projectName = project.getName();
				String description = project.getDescription();
				int[] buildNumbers = project.getBuildNumbers();
				Build completedBuild = project.getCompletedBuild();
				Build currentBuild = project.getCurrentBuild();
				State state = project.getState();

				if (LOG.isDebugEnabled()) {
					LOG.debug("plugin - " + buildPlugin.getClass().getSimpleName());
					LOG.debug("projectName:" + projectName);
					LOG.debug("description:" + description);
					LOG.debug("buildNumbers:" + Arrays.toString(buildNumbers));
					LOG.debug("completedBuild:" + completedBuild);
					LOG.debug("currentBuild: " + currentBuild);
					LOG.debug("state:" + state);
				}

				if (buildNumbers != null) {
					projectToMerge.setBuildNumbers(buildNumbers);
				}
				if (completedBuild != null) {
					projectToMerge.setCompletedBuild(completedBuild);
				}
				if (currentBuild != null) {
					projectToMerge.setCurrentBuild(currentBuild);
				}
				if (StringUtils.isNotBlank(description)) {
					projectToMerge.setDescription(description);
				}
				if (StringUtils.isNotBlank(projectName)) {
					projectToMerge.setName(projectName);
				}
				if (state != null) {
					projectToMerge.setState(state);
				}
			}
		} catch (ProjectNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug(e.getMessage());
			}
		}
	}

	public void enhanceWithQualityAnalysis(Project analyzedProject, ConnectionPlugin qualityPlugin,
	        String... metrics) {
		ProjectId projectId = analyzedProject.getProjectId();
		Build build = analyzedProject.getCompletedBuild();

		QualityResult qualityAnalysis = qualityPlugin.analyzeQuality(projectId, metrics);
		TestResult unitTestsAnalysis = qualityPlugin.analyzeUnitTests(projectId);
		TestResult integrationTestsAnalysis = qualityPlugin.analyzeIntegrationTests(projectId);

		QualityResult qualityResultToMerge = analyzedProject.getQualityResult();

		TestResult unitTestResultToMerge = null;
		TestResult integrationTestResultToMerge = null;
		if (build != null) {
			unitTestResultToMerge = build.getUnitTestResult();
			integrationTestResultToMerge = build.getIntegrationTestResult();
		}

		if (qualityAnalysis != null) {
			mergeQualityAnalysis(qualityResultToMerge, qualityAnalysis);
		}
		if (unitTestsAnalysis != null && unitTestResultToMerge != null) {
			mergeTestAnalysis(unitTestResultToMerge, unitTestsAnalysis);
		}
		if (integrationTestsAnalysis != null && integrationTestResultToMerge != null) {
			mergeTestAnalysis(integrationTestResultToMerge, integrationTestsAnalysis);
		}
	}

	private void mergeQualityAnalysis(QualityResult qualityResultToMerge, QualityResult qualityAnalysis) {
		for (Entry<String, QualityMeasure> entry : qualityAnalysis.getMeasures()) {
			qualityResultToMerge.add(entry.getKey(), entry.getValue());
		}
	}

	private void mergeTestAnalysis(TestResult testResultToMerge, TestResult testsAnalysis) {
		if (testsAnalysis.getCoverage() > 0) {
			testResultToMerge.setCoverage(testsAnalysis.getCoverage());
		}
		if (testsAnalysis.getFailCount() > 0) {
			testResultToMerge.setFailCount(testsAnalysis.getFailCount());
		}
		if (testsAnalysis.getPassCount() > 0) {
			testResultToMerge.setPassCount(testsAnalysis.getPassCount());
		}
		if (testsAnalysis.getSkipCount() > 0) {
			testResultToMerge.setSkipCount(testsAnalysis.getSkipCount());
		}
	}

}
