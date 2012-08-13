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
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import net.awired.clients.teamcity.TeamCity;
import net.awired.clients.teamcity.exception.TeamCityBuildNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityBuildTypeNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityProjectsNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityBuild;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityChange;
import net.awired.clients.teamcity.resource.TeamCityProject;
import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class TeamCityConnectionTest {

    TeamCityConnection teamCityConnection;

    @Mock
    TeamCity teamCity;

    @Before
    public void init() {
        teamCityConnection = createConnectionPlugin();
    }

    @Test(expected = NullPointerException.class)
    public void should_fail_when_passing_null() {
        teamCityConnection.connect(null, "", "");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_fail_when_passing_empty_string() {
        teamCityConnection.connect("", "", "");
    }

    @Test
    public void should_find_state_build() throws Exception {
        TeamCityBuild build = new TeamCityBuild();
        build.setStatus("SUCCESS");
        when(teamCity.findBuild(anyString(), anyString())).thenReturn(build);

        SoftwareProjectId projectId = new SoftwareProjectId("projectId");
        BuildState state = teamCityConnection.getBuildState(projectId, "1234");

        assertEquals(BuildState.SUCCESS, state);
    }

    @Test
    public void should_get_a_disabled_project() throws Exception {
        TeamCityBuildType project = new TeamCityBuildType();
        project.setPaused(true);

        when(teamCity.findBuildType(anyString())).thenReturn(project);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = teamCityConnection.isProjectDisabled(softwareProjectId);

        assertTrue(isDisabled);
    }

    @Test
    public void should_get_an_enabled_project() throws Exception {
        TeamCityBuildType project = new TeamCityBuildType();
        project.setPaused(false);

        when(teamCity.findBuildType(anyString())).thenReturn(project);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = teamCityConnection.isProjectDisabled(softwareProjectId);

        assertFalse(isDisabled);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Throwable notFound = new TeamCityBuildTypeNotFoundException("not found", null);
        when(teamCity.findBuildType(anyString())).thenThrow(notFound);

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
        List<Commiter> commiters = teamCityConnection.getBuildCommiters(softwareProjectId, "1");
        Commiter commiter = commiters.get(0);
        assertEquals("npryce", commiter.getName());
    }

    @Test
    public void should_get_description() throws Exception {
        TeamCityBuildType buildType = new TeamCityBuildType();
        buildType.setDescription("description");

        when(teamCity.findBuildType(anyString())).thenReturn(buildType);
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String description = teamCityConnection.getDescription(softwareProjectId);

        assertEquals("description", description);
    }

    @Test
    public void should_get_name() throws Exception {
        TeamCityBuildType project = new TeamCityBuildType();
        project.setName("name");

        when(teamCity.findBuildType(anyString())).thenReturn(project);
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

        BuildTime buildTime = teamCityConnection.getBuildTime(softwareProjectId(), "1");

        assertEquals(1000, buildTime.getDuration());
        assertNotNull(buildTime.getStartTime());
    }

    @Test
    public void connection_should_be_closed() {
        assertFalse(teamCityConnection.isClosed());
        teamCityConnection.close();
        assertTrue(teamCityConnection.isClosed());
    }

    @Test
    public void should_throw_exception_when_getting_maven_id() throws Exception {
        SoftwareProjectId softwareProjectId = softwareProjectId();
        String projectId = softwareProjectId.getProjectId();
        when(teamCity.findMavenId(projectId)).thenReturn("groupId:artifactId");

        String mavenId = teamCityConnection.getMavenId(softwareProjectId);

        assertEquals("groupId:artifactId", mavenId);
    }

    @Test
    public void should_get_is_building() throws Exception {
        TeamCityBuildItem buildItem = new TeamCityBuildItem();
        buildItem.setBuildTypeId("projectId");
        buildItem.setId("1");
        List<TeamCityBuildItem> runningBuilds = new ArrayList<TeamCityBuildItem>();
        runningBuilds.add(buildItem);

        TeamCityBuilds builds = new TeamCityBuilds();
        builds.setBuilds(runningBuilds);

        when(teamCity.findRunningBuilds()).thenReturn(builds);

        boolean isBuilding = teamCityConnection.isBuilding(softwareProjectId(), "1");

        assertTrue(isBuilding);
    }

    @Ignore
    @Test
    public void should_get_last_build_number() throws Exception {
        TeamCityBuilds buildList = new TeamCityBuilds();
        for (int i = 1; i <= 10; i++) {
            TeamCityBuildItem item = new TeamCityBuildItem();
            item.setNumber(Integer.toString(i));
            buildList.getBuilds().add(item);
        }
        when(teamCity.findBuildList(anyString())).thenReturn(buildList);
        when(teamCity.findRunningBuilds()).thenThrow(new TeamCityBuildNotFoundException(""));

        TeamCityBuildType buildType = new TeamCityBuildType();

        List<TeamCityBuildType> buildTypes = new ArrayList<TeamCityBuildType>();
        TeamCityProject project = new TeamCityProject();
        project.setBuildTypes(buildTypes);
        project.getBuildTypes().add(buildType);

        when(teamCity.findProject(anyString())).thenReturn(project);

        String lastBuildId = teamCityConnection.getLastBuildId(softwareProjectId());

        assertEquals("10", lastBuildId);
    }

    @Ignore
    @Test
    public void should_list_all_project_ids() throws Exception {
        addTwoProjects();

        Map<SoftwareProjectId, String> projectIds = teamCityConnection.listSoftwareProjectIds();

        assertEquals("name1", projectIds.get(new SoftwareProjectId("id1")));
        assertEquals("name2", projectIds.get(new SoftwareProjectId("id2")));

    }

    @Test
    public void should_identify_project() throws Exception {
        addTwoProjects();

        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("name1");
        SoftwareProjectId softwareProjectId = teamCityConnection.identify(projectKey);
        assertEquals("id1", softwareProjectId.getProjectId());
    }

    private void addTwoProjects() throws TeamCityProjectsNotFoundException {
        TeamCityProject project1 = new TeamCityProject();
        project1.setId("id1");
        project1.setName("name1");

        TeamCityProject project2 = new TeamCityProject();
        project2.setId("id2");
        project2.setName("name2");

        List<TeamCityProject> projects = new ArrayList<TeamCityProject>();
        projects.add(project1);
        projects.add(project2);

        when(teamCity.findAllProjects()).thenReturn(projects);
    }

    private SoftwareProjectId softwareProjectId() {
        return new SoftwareProjectId("projectId");
    }

    private TeamCityConnection createConnectionPlugin() {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect("http://", "login", "password");
        connectionPlugin.teamCity = teamCity;
        return connectionPlugin;
    }
}
