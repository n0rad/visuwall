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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.teamcityclient.TeamCityFinder;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildListNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;


public class TeamCityFinderExceptionTest {

    TeamCityFinder teamCityFinder;

    @Mock
    GenericSoftwareClient client;

    @Mock
    TeamCityUrlBuilder urlBuilder;

    Throwable resourceNotFound = new ResourceNotFoundException("not found");

    @Test(expected = TeamCityProjectsNotFoundException.class)
    public void should_throw_exception_when_projects_cant_be_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(resourceNotFound);

        teamCityFinder.getProjects();
    }

    @Test(expected = TeamCityProjectNotFoundException.class)
    public void should_throw_exception_when_project_cant_be_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(resourceNotFound);

        teamCityFinder.getProject("id1");
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_project_id_in_get_project_method() throws TeamCityProjectNotFoundException {
        teamCityFinder.getProject(null);
    }

    @Test(expected = TeamCityBuildNotFoundException.class)
    public void should_throw_exception_when_build_cant_be_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(resourceNotFound);

        teamCityFinder.getBuild(1);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_pass_negative_number_in_get_build_method() throws TeamCityBuildNotFoundException {
        teamCityFinder.getBuild(-1);
    }

    @Test(expected = TeamCityBuildListNotFoundException.class)
    public void should_throw_exception_when_build_list_cant_be_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(resourceNotFound);

        teamCityFinder.getBuildList("buildTypeId");
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null__as_buid_type_id_in_get_build_list_method() throws Exception {
        teamCityFinder.getBuildList(null);
    }

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        teamCityFinder = new TeamCityFinder(client, urlBuilder);
    }

}
