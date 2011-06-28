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

package net.awired.visuwall.teamcityclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.List;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.teamcityclient.TeamCityFinder;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildItem;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityProjects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class TeamCityFinderTest {

    TeamCityFinder teamCityFinder;

    @Mock
    GenericSoftwareClient client;

    @Mock
    TeamCityUrlBuilder urlBuilder;

    @Test
    public void should_get_projects() throws Exception {
        TeamCityProject tcp1 = new TeamCityProject();
        tcp1.setId("id1");
        TeamCityProject tcp2 = new TeamCityProject();
        tcp2.setId("id2");

        TeamCityProjects teamcityProjects = new TeamCityProjects();
        teamcityProjects.getProjects().add(tcp1);
        teamcityProjects.getProjects().add(tcp2);
        when(client.resource(anyString(), any(Class.class))).thenReturn(teamcityProjects);

        List<TeamCityProject> projects = teamCityFinder.getProjects();
        
        assertEquals(2, projects.size());
        assertTrue(projects.contains(tcp1));
        assertTrue(projects.contains(tcp2));
    }

    @Test
    public void should_get_project() throws Exception {
        TeamCityProject tcp1 = new TeamCityProject();
        tcp1.setId("id1");

        when(client.resource(anyString(), any(Class.class))).thenReturn(tcp1);

        TeamCityProject project = teamCityFinder.getProject("id1");

        assertEquals(tcp1, project);
    }

    @Test
    public void should_get_build() throws Exception {
        TeamCityBuild teamcityBuild = new TeamCityBuild();
        teamcityBuild.setId("1");

        when(client.resource(anyString(), any(Class.class))).thenReturn(teamcityBuild);

        TeamCityBuild build = teamCityFinder.getBuild(1);

        assertEquals(teamcityBuild, build);
    }

    @Test
    public void should_get_build_list() throws Exception {
        TeamCityBuildItem tcbi = new TeamCityBuildItem();
        tcbi.setId("id");

        TeamCityBuilds tcb = new TeamCityBuilds();
        tcb.getBuilds().add(tcbi);
        
        when(client.resource(anyString(), any(Class.class))).thenReturn(tcb);
        
        TeamCityBuilds teamCityBuilds = teamCityFinder.getBuildList("buildTypeId");

        assertEquals(tcb.getBuilds().size(), teamCityBuilds.getBuilds().size());

        TeamCityBuildItem buildItem = teamCityBuilds.getBuilds().get(0);
        assertEquals(tcbi, buildItem);
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        teamCityFinder = new TeamCityFinder(client, urlBuilder);
    }

}
