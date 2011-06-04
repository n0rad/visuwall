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

package net.awired.visuwall.plugin.hudson;

import static net.awired.visuwall.IntegrationTestData.HUDSON_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonConnectionPluginIT {

	static HudsonConnectionPlugin hudsonConnectionPlugin = new HudsonConnectionPlugin();

	@BeforeClass
	public static void setUp() {
		hudsonConnectionPlugin.connect(IntegrationTestData.HUDSON_URL);
	}

	@Test
	public void should_find_all_projects() {
		List<ProjectId> projects = hudsonConnectionPlugin.findAllProjects();
		assertFalse(projects.isEmpty());
	}

	@Test
	public void should_find_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "neverbuild");
		Project project = hudsonConnectionPlugin.findProject(projectId);
		assertNotNull(project);
	}

	@Test
	public void should_find_build_by_name_and_build_number() throws BuildNotFoundException, ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "struts");
		Build build = hudsonConnectionPlugin.findBuildByBuildNumber(projectId, 3);
		assertNotNull(build);
	}

	@Test
	public void should_find_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "struts");
		int buildNumber = hudsonConnectionPlugin.getLastBuildNumber(projectId);
		assertEquals(4, buildNumber);
	}

	@Test
	public void should_verify_not_building_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "struts");
		boolean building = hudsonConnectionPlugin.isBuilding(projectId);
		assertFalse(building);
	}

	@Test
	public void should_verify_state() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "struts");
		State state = hudsonConnectionPlugin.getState(projectId);
		assertEquals(State.SUCCESS, state);
	}

	@Test
	public void should_populate_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(HUDSON_ID, "struts");
		Project project = hudsonConnectionPlugin.findProject(projectId);
		hudsonConnectionPlugin.populate(project);
		assertEquals("struts", project.getName());
	}

}
