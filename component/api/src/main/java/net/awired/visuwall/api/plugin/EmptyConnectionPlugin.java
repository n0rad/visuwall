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

package net.awired.visuwall.api.plugin;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;

public class EmptyConnectionPlugin implements ConnectionPlugin {

	private List<ProjectId> projects = new ArrayList<ProjectId>();
	private List<String> projectNames = new ArrayList<String>();
	private QualityResult qualityResult = new QualityResult();
	private TestResult unitTestResult = new TestResult();
	private TestResult integrationTestResult = new TestResult();
	private HashMap<String, List<QualityMetric>> metricsByCategory = new HashMap<String, List<QualityMetric>>();
	private List<String> views = new ArrayList<String>();

	@Override
	public List<ProjectId> findAllProjects() {
		return projects;
	}

	@Override
	public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
		throw new ProjectNotFoundException("projectId: " + projectId + " not found");
	}

	@Override
	public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
	        ProjectNotFoundException {
		throw new BuildNotFoundException("build #" + buildNumber + " of projectId:" + projectId + " not found");
	}

	@Override
	public void populate(Project project) throws ProjectNotFoundException {
		throw new ProjectNotFoundException("project: " + project + " not found");
	}

	@Override
	public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
		throw new ProjectNotFoundException("projectId: " + projectId + " not found");
	}

	@Override
	public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
		throw new ProjectNotFoundException("projectId: " + projectId + " not found");
	}

	@Override
	public State getState(ProjectId projectId) throws ProjectNotFoundException {
		throw new ProjectNotFoundException("projectId: " + projectId + " not found");
	}

	@Override
	public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
		throw new ProjectNotFoundException("projectId: " + projectId + " not found");
	}

	@Override
	public List<String> findProjectNames() {
		return projectNames;
	}

	@Override
	public QualityResult analyzeQuality(ProjectId projectId, String... metrics) {
		return qualityResult;
	}

	@Override
	public TestResult analyzeUnitTests(ProjectId projectId) {
		return unitTestResult;
	}

	@Override
	public TestResult analyzeIntegrationTests(ProjectId projectId) {
		return integrationTestResult;
	}

	@Override
	public boolean contains(ProjectId projectId) {
		return false;
	}

	@Override
	public Map<String, List<QualityMetric>> getMetricsByCategory() {
		return metricsByCategory;
	}

	@Override
	public List<String> findViews() {
		return views;
	}

	@Override
	public List<String> findProjectsByView(String viewName) throws ViewNotFoundException {
		throw new ViewNotFoundException("view called " + viewName + " not found");
	}

}
