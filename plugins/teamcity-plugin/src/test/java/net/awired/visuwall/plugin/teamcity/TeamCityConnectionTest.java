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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import net.awired.clients.teamcity.TeamCity;
import net.awired.clients.teamcity.exception.TeamCityProjectNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityProjectsNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityBuild;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuildTypes;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityChange;
import net.awired.clients.teamcity.resource.TeamCityProject;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class TeamCityConnectionTest {

    private TeamCity teamCity;
    private TeamCityConnection teamCityConnection;

    @Before
    public void init() {
        teamCity = Mockito.mock(TeamCity.class);
        teamCityConnection = createConnectionPlugin();
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_passing_null() {
        teamCityConnection.connect(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_when_passing_empty_string() {
        teamCityConnection.connect("");
    }

    @Test
    public void should_call_find_project_names() throws TeamCityProjectsNotFoundException {
        teamCityConnection.findProjectNames();
        verify(teamCity).findProjectNames();
    }

    @Test
    public void should_find_state_build() throws Exception {
        TeamCityBuild build = new TeamCityBuild();
        build.setStatus(States.SUCCESS);
        when(teamCity.findBuild(anyString(), anyString())).thenReturn(build);

        SoftwareProjectId projectId = new SoftwareProjectId("projectId");
        State state = teamCityConnection.getBuildState(projectId, 1234);

        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_get_a_disabled_project() throws Exception {
        TeamCityProject project = new TeamCityProject();
        project.setArchived(true);

        when(teamCity.findProject(anyString())).thenReturn(project);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = teamCityConnection.isProjectDisabled(softwareProjectId);

        assertTrue(isDisabled);
    }

    @Test
    public void should_get_an_enabled_project() throws Exception {
        TeamCityProject project = new TeamCityProject();
        project.setArchived(false);

        when(teamCity.findProject(anyString())).thenReturn(project);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = teamCityConnection.isProjectDisabled(softwareProjectId);

        assertFalse(isDisabled);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Throwable notFound = new TeamCityProjectNotFoundException("not found");
        when(teamCity.findProject(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        teamCityConnection.isProjectDisabled(softwareProjectId);
    }

    @Test
    public void should_get_commiters() throws Exception {
        TeamCityChange change = new TeamCityChange();
        change.setUsername("npryce");

        List<TeamCityChange> changes = new ArrayList<TeamCityChange>();
        changes.add(change);

        when(teamCity.findChanges(anyInt())).thenReturn(changes);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        List<Commiter> commiters = teamCityConnection.getBuildCommiters(softwareProjectId, 1);
        Commiter commiter = commiters.get(0);
        assertEquals("npryce", commiter.getName());
    }

    @Test
    public void should_get_description() throws Exception {
        TeamCityProject project = new TeamCityProject();
        project.setDescription("description");

        when(teamCity.findProject(anyString())).thenReturn(project);
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String description = teamCityConnection.getDescription(softwareProjectId);

        assertEquals("description", description);
    }

    @Test
    public void should_get_name() throws Exception {
        TeamCityProject project = new TeamCityProject();
        project.setName("name");

        when(teamCity.findProject(anyString())).thenReturn(project);
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String name = teamCityConnection.getName(softwareProjectId);

        assertEquals("name", name);
    }

    @Test
    public void should_get_build_time() throws Exception {
        TeamCityBuild build = new TeamCityBuild();
        build.setStartDate("20110302T171940+0300");
        build.setFinishDate("20110302T171941+0300");

        when(teamCity.findBuild(anyString(), anyString())).thenReturn(build);

        BuildTime buildTime = teamCityConnection.getBuildTime(softwareProjectId(), 1);

        assertEquals(1000, buildTime.getDuration());
        assertNotNull(buildTime.getStartTime());
    }

    @Test
    public void connection_should_be_closed() {
        assertFalse(teamCityConnection.isClosed());
        teamCityConnection.close();
        assertTrue(teamCityConnection.isClosed());
    }

    @Test(expected = MavenIdNotFoundException.class)
    public void should_throw_exception_when_getting_maven_id() throws Exception {
        teamCityConnection.getMavenId(softwareProjectId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_getting_estimated_finish_time() throws Exception {
        teamCityConnection.getEstimatedFinishTime(softwareProjectId(), 1);
    }

    @Test
    public void should_get_is_building() throws Exception {
        TeamCityBuild build = new TeamCityBuild();
        build.setFinishDate("20310302T171940+0300");
        when(teamCity.findBuild(anyString(), anyString())).thenReturn(build);

        boolean isBuilding = teamCityConnection.isBuilding(softwareProjectId(), 1);

        assertTrue(isBuilding);
    }

    @Test
    public void should_get_last_build_number() throws Exception {
        TeamCityBuilds buildList = new TeamCityBuilds();
        for (int i = 1; i <= 10; i++) {
            TeamCityBuildItem item = new TeamCityBuildItem();
            item.setNumber(Integer.toString(i));
            buildList.getBuilds().add(item);
        }
        when(teamCity.findBuildList(anyString())).thenReturn(buildList);

        TeamCityBuildType buildType = new TeamCityBuildType();

        TeamCityBuildTypes buildTypes = new TeamCityBuildTypes();
        TeamCityProject project = new TeamCityProject();
        project.setBuildTypes(buildTypes);
        project.getBuildTypes().add(buildType);

        when(teamCity.findProject(anyString())).thenReturn(project);

        int lastBuildNumber = teamCityConnection.getLastBuildNumber(softwareProjectId());

        assertEquals(10, lastBuildNumber);
    }

    private SoftwareProjectId softwareProjectId() {
        return new SoftwareProjectId("projectId");
    }

    private TeamCityConnection createConnectionPlugin() {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect("http://");
        connectionPlugin.teamCity = teamCity;
        return connectionPlugin;
    }
}
