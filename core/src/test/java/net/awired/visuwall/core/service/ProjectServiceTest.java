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

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.core.domain.PluginHolder;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectServiceTest {

	ProjectService projectService;

	@Before
	public void init() {
		projectService = new ProjectService();
		ProjectEnhancerService projectMergeService = Mockito.mock(ProjectEnhancerService.class);
		projectService.projectMergeService = projectMergeService;
	}

	@Test(expected = NullPointerException.class)
	public void should_not_accept_null_parameter() throws NotCreatedException {
		projectService.updateProject(null, new Project("test"));
		projectService.updateProject(new PluginHolder(), null);
	}

	public PluginHolder getPluginHolder() {
		PluginHolder pluginHolder = new PluginHolder();
		BuildConnectionPlugin buildConnectionPlugin = Mockito.mock(BuildConnectionPlugin.class);
		QualityConnectionPlugin qualityConnectionPlugin = Mockito.mock(QualityConnectionPlugin.class);
		pluginHolder.addBuildService(buildConnectionPlugin);
		pluginHolder.addQualityService(qualityConnectionPlugin);

		List<ProjectId> projectIds = new ArrayList<ProjectId>();
		projectIds.add(new ProjectId());
		projectIds.add(new ProjectId());
		when(buildConnectionPlugin.findAllProjects()).thenReturn(projectIds);
		return pluginHolder;
	}

	@Ignore
	@Test
	public void test() {
		Wall wall = new Wall();
		wall.setPluginHolder(getPluginHolder());
		projectService.updateWallProjects(wall);
	}

	@Test
	public void should_call_merge_for_plugins() {
		PluginHolder pluginHolder = getPluginHolder();
		Project project = new Project("test");
		projectService.updateProject(pluginHolder, project);

		Mockito.verify(projectService.projectMergeService).enhanceWithBuildInformations(project,
		        pluginHolder.getBuildServices().iterator().next());
		Mockito.verify(projectService.projectMergeService).enhanceWithQualityAnalysis(project,
		        pluginHolder.getQualityServices().iterator().next(), projectService.metrics);
	}
}
