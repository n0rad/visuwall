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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
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

        SoftwareProjectId projectId = new SoftwareProjectId("projectId");
        State state = teamCityConnection.getBuildState(projectId, 0);

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

    private TeamCityConnection createConnectionPlugin() {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect("http://");
        connectionPlugin.teamCity = teamCity;
        return connectionPlugin;
    }
}
