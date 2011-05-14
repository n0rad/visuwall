/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.core.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectEnhancerServiceWithQualityPluginTest {

	ProjectEnhancerService projectEnhancerService = new ProjectEnhancerService();

	Project projectToEnhance;

	@Before
	public void init() {
		ProjectId projectId = new ProjectId();
		projectId.addId("id", "value");
		projectToEnhance = new Project(projectId);

		Build completedBuild = new Build();

		projectToEnhance.setCompletedBuild(completedBuild);
	}

	@Test
	public void should_merge_with_one_build_plugin() throws ProjectNotFoundException {
		QualityConnectionPlugin qualityPlugin = Mockito.mock(QualityConnectionPlugin.class);

		QualityResult qualityResult = new QualityResult();
		QualityMeasure coverage = new QualityMeasure();
		coverage.setName("coverage");
		coverage.setValue(29D);
		coverage.setFormattedValue("29%");
		qualityResult.add("coverage", coverage);

		ProjectId projectId = projectToEnhance.getProjectId();
		when(qualityPlugin.analizeQuality(projectId)).thenReturn(qualityResult);
		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin);

		assertEquals(coverage, projectToEnhance.getQualityResult().getMeasure("coverage"));
	}

	@Test
	public void should_merge_with_two_build_plugins() throws ProjectNotFoundException {
		QualityConnectionPlugin qualityPlugin1 = Mockito.mock(QualityConnectionPlugin.class);
		QualityConnectionPlugin qualityPlugin2 = Mockito.mock(QualityConnectionPlugin.class);

		QualityResult qualityResult1 = new QualityResult();
		QualityMeasure coverage = new QualityMeasure();
		coverage.setName("coverage");
		coverage.setValue(29D);
		coverage.setFormattedValue("29%");
		qualityResult1.add("coverage", coverage);

		QualityResult qualityResult2 = new QualityResult();
		QualityMeasure violations = new QualityMeasure();
		violations.setName("violations");
		violations.setValue(1D);
		violations.setFormattedValue("1%");
		qualityResult2.add("violations", violations);

		ProjectId projectId = projectToEnhance.getProjectId();
		when(qualityPlugin1.analizeQuality(projectId)).thenReturn(qualityResult1);
		when(qualityPlugin2.analizeQuality(projectId)).thenReturn(qualityResult2);
		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin1);
		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin2);

		assertEquals(coverage, projectToEnhance.getQualityResult().getMeasure("coverage"));
		assertEquals(violations, projectToEnhance.getQualityResult().getMeasure("violations"));
	}

	@Test
	public void last_plugin_is_always_right() throws ProjectNotFoundException {
		QualityConnectionPlugin qualityPlugin1 = Mockito.mock(QualityConnectionPlugin.class);
		QualityConnectionPlugin qualityPlugin2 = Mockito.mock(QualityConnectionPlugin.class);

		QualityResult qualityResult1 = new QualityResult();
		QualityMeasure coverage = new QualityMeasure();
		coverage.setName("coverage");
		coverage.setValue(29D);
		coverage.setFormattedValue("29%");
		qualityResult1.add("coverage", coverage);

		QualityResult qualityResult2 = new QualityResult();
		QualityMeasure coverage2 = new QualityMeasure();
		coverage2.setName("coverage");
		coverage2.setValue(1D);
		coverage2.setFormattedValue("1%");
		qualityResult2.add("coverage", coverage2);

		ProjectId projectId = projectToEnhance.getProjectId();
		when(qualityPlugin1.analizeQuality(projectId)).thenReturn(qualityResult1);
		when(qualityPlugin2.analizeQuality(projectId)).thenReturn(qualityResult2);
		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin1);
		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin2);

		assertEquals(coverage2, projectToEnhance.getQualityResult().getMeasure("coverage"));
	}

	@Test
	public void should_enhance_unit_test_results() {
		TestResult testResult = new TestResult();
		testResult.setCoverage(4);
		testResult.setFailCount(1);
		testResult.setPassCount(2);
		testResult.setSkipCount(3);

		QualityConnectionPlugin qualityPlugin = Mockito.mock(QualityConnectionPlugin.class);
		when(qualityPlugin.analyzeUnitTests(projectToEnhance.getProjectId())).thenReturn(testResult);

		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin);

		Build build = projectToEnhance.getCompletedBuild();

		TestResult enhancedTestResult = build.getUnitTestResult();
		assertEquals(1, enhancedTestResult.getFailCount());
		assertEquals(2, enhancedTestResult.getPassCount());
		assertEquals(3, enhancedTestResult.getSkipCount());
		assertEquals(4.0, enhancedTestResult.getCoverage(), 0);
	}

	@Test
	public void should_enhance_integration_test_results() {
		TestResult testResult = new TestResult();
		testResult.setCoverage(4);
		testResult.setFailCount(1);
		testResult.setPassCount(2);
		testResult.setSkipCount(3);

		QualityConnectionPlugin qualityPlugin = Mockito.mock(QualityConnectionPlugin.class);
		when(qualityPlugin.analyzeIntegrationTests(projectToEnhance.getProjectId())).thenReturn(testResult);

		projectEnhancerService.enhanceWithQualityAnalysis(projectToEnhance, qualityPlugin);

		Build build = projectToEnhance.getCompletedBuild();

		TestResult enhancedTestResult = build.getIntegrationTestResult();
		assertEquals(1, enhancedTestResult.getFailCount());
		assertEquals(2, enhancedTestResult.getPassCount());
		assertEquals(3, enhancedTestResult.getSkipCount());
		assertEquals(4.0, enhancedTestResult.getCoverage(), 0);
	}

}
