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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectEnhancerServiceWithBuildPluginTest {

	ProjectEnhancerService projectEnhancerService = new ProjectEnhancerService();

	Project projectToEnhance;

	@Before
	public void init() {
		ProjectId projectId = new ProjectId();
		projectId.addId("id", "value");
		projectToEnhance = new Project(projectId);
	}

	@Test
	public void should_merge_with_one_build_plugin() throws ProjectNotFoundException {
		BuildConnectionPlugin buildPlugin = Mockito.mock(BuildConnectionPlugin.class);

		Build completedBuild = new Build();
		Build currentBuild = new Build();

		Project projectFromBuildPlugin = new Project("name");
		projectFromBuildPlugin.setBuildNumbers(new int[] { 1, 2, 3 });
		projectFromBuildPlugin.setCompletedBuild(completedBuild);
		projectFromBuildPlugin.setCurrentBuild(currentBuild);
		projectFromBuildPlugin.setDescription("description");
		projectFromBuildPlugin.setState(State.UNSTABLE);

		ProjectId projectId = projectToEnhance.getProjectId();
		when(buildPlugin.findProject(projectId)).thenReturn(projectFromBuildPlugin);
		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin);

		assertArrayEquals(new int[] { 1, 2, 3 }, projectToEnhance.getBuildNumbers());
		assertEquals(completedBuild, projectToEnhance.getCompletedBuild());
		assertEquals(currentBuild, projectToEnhance.getCurrentBuild());
		assertEquals("description", projectToEnhance.getDescription());
		assertEquals(State.UNSTABLE, projectToEnhance.getState());
		assertEquals("name", projectToEnhance.getName());
	}

	@Test
	public void should_merge_with_two_build_plugins() throws ProjectNotFoundException {
		BuildConnectionPlugin buildPlugin1 = Mockito.mock(BuildConnectionPlugin.class);
		BuildConnectionPlugin buildPlugin2 = Mockito.mock(BuildConnectionPlugin.class);

		Project projectFromBuildPlugin1 = new Project("name1");
		projectFromBuildPlugin1.setDescription("description");

		Project projectFromBuildPlugin2 = new Project("name2");

		ProjectId projectId = projectToEnhance.getProjectId();
		when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
		when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin1);
		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin2);

		assertEquals("description", projectToEnhance.getDescription());
		assertEquals("name2", projectToEnhance.getName());
	}

	@Test
	public void last_plugin_is_always_right() throws ProjectNotFoundException {
		BuildConnectionPlugin buildPlugin1 = Mockito.mock(BuildConnectionPlugin.class);
		BuildConnectionPlugin buildPlugin2 = Mockito.mock(BuildConnectionPlugin.class);

		Project projectFromBuildPlugin1 = new Project("name1");
		projectFromBuildPlugin1.setDescription("description1");

		Project projectFromBuildPlugin2 = new Project("name2");
		projectFromBuildPlugin2.setDescription("description2");

		ProjectId projectId = projectToEnhance.getProjectId();
		when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
		when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin1);
		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin2);

		assertEquals("description2", projectToEnhance.getDescription());
	}

	@Test
	public void should_not_fail_if_project_is_not_found() throws ProjectNotFoundException {
		BuildConnectionPlugin buildPlugin = Mockito.mock(BuildConnectionPlugin.class);
		ProjectId projectId = projectToEnhance.getProjectId();
		when(buildPlugin.findProject(projectId)).thenThrow(new ProjectNotFoundException("project not found"));
		projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin);
	}
}
