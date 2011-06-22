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

package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildItem;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityStatus;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class TeamCityConnectionTest {

	private TeamCity teamCity;
	private TeamCityConnection connectionPlugin;

	@Before
	public void init() {
		teamCity = Mockito.mock(TeamCity.class);
		connectionPlugin = createConnectionPlugin();
	}

	@Test(expected = NullPointerException.class)
	public void should_fail_when_passing_null() {
		connectionPlugin.connect(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_when_passing_empty_string() {
		connectionPlugin.connect("");
	}

	@Test
	public void should_call_find_project_names() throws TeamCityProjectsNotFoundException {
		connectionPlugin.findProjectNames();
		verify(teamCity).findProjectNames();
	}

	@Test
	public void should_build_one_project_id() throws TeamCityProjectsNotFoundException {
		List<TeamCityProject> teamCityProjects = new ArrayList<TeamCityProject>();
		TeamCityProject teamCityProject = new TeamCityProject();
		teamCityProject.setName("projectName");
		teamCityProject.setId("projectId");
		teamCityProjects.add(teamCityProject);
		
		when(teamCity.findAllProjects()).thenReturn(teamCityProjects );
		
		List<ProjectId> projects = connectionPlugin.findAllProjects();
		ProjectId projectId = projects.get(0);
		
		assertEquals("projectName", projectId.getName());
		assertEquals("projectId", projectId.getId(TeamCityConnection.TEAMCITY_ID));
	}

	@Ignore
	@Test
	public void should_find_state_build() throws Exception {
		TeamCityBuildItem buildItem = new TeamCityBuildItem();
		buildItem.setStatus(TeamCityStatus.SUCCESS);

		List<TeamCityBuildItem> buildItems = new ArrayList<TeamCityBuildItem>();
		buildItems.add(buildItem);

		TeamCityBuilds builds = new TeamCityBuilds();
		builds.setBuilds(buildItems);

		TeamCityProject teamCityProject = new TeamCityProject();
		when(teamCity.findProject("projectId")).thenReturn(teamCityProject);
		when(teamCity.findBuildList("bt297"));

		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnection.TEAMCITY_ID, "projectId");
		State state = connectionPlugin.getLastBuildState(projectId);

		assertEquals(State.SUCCESS, state);
	}

	private TeamCityConnection createConnectionPlugin() {
	    TeamCityConnection connectionPlugin = new TeamCityConnection();
		connectionPlugin.connect("http://");
		connectionPlugin.teamCity = teamCity;
	    return connectionPlugin;
    }
}
