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

import java.util.Date;
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
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;

public class EmptyConnectionPlugin implements ConnectionPlugin {

	NotImplementedOperationException notImplementedOperationException = new NotImplementedOperationException(
	        "This operation is not implemented in this plugin");

	@Override
	public QualityResult analyzeQuality(ProjectId projectId, String... metrics) throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public TestResult analyzeUnitTests(ProjectId projectId) throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public TestResult analyzeIntegrationTests(ProjectId projectId) throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public boolean contains(ProjectId projectId) throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public Map<String, List<QualityMetric>> getMetricsByCategory() throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public List<ProjectId> findAllProjects() throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public Project findProject(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws NotImplementedOperationException,
	        BuildNotFoundException, ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public void populate(Project project) throws NotImplementedOperationException, ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public Date getEstimatedFinishTime(ProjectId projectId) throws NotImplementedOperationException,
	        ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public boolean isBuilding(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public State getState(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public int getLastBuildNumber(ProjectId projectId) throws NotImplementedOperationException,
	        ProjectNotFoundException, BuildNotFoundException {
		throw notImplementedOperationException;
	}

	@Override
	public List<String> findProjectNames() throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public List<String> findViews() throws NotImplementedOperationException {
		throw notImplementedOperationException;
	}

	@Override
	public List<String> findProjectsByView(String viewName) throws NotImplementedOperationException,
	        ViewNotFoundException {
		throw notImplementedOperationException;
	}

}
