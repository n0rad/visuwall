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

package net.awired.clients.teamcity;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class TeamCityUrlBuilderTest {

    private static final String TEAM_CITY_URL = "http://teamcity.jetbrains.com";

    TeamCityUrlBuilder builder = new TeamCityUrlBuilder(TEAM_CITY_URL);

    @Test
    public void should_create_valid_projects_url() {
        String projectsUrl = builder.getProjects();
        assertEquals(TEAM_CITY_URL + "/app/rest/projects", projectsUrl);
    }

    @Test
    public void should_create_valid_project_url() {
        String projectUrl = builder.getProject("project54");
        assertEquals(TEAM_CITY_URL + "/app/rest/projects/id:project54", projectUrl);
    }

    @Test
    public void should_create_valid_build_type_url() {
        String buildTypeUrl = builder.getBuildType("bt296");
        assertEquals(TEAM_CITY_URL + "/app/rest/buildTypes/id:bt296", buildTypeUrl);
    }

    @Test
    public void should_create_valid_build_url() {
        String buildTypeUrl = builder.getBuild(47068);
        assertEquals(TEAM_CITY_URL + "/app/rest/builds/id:47068", buildTypeUrl);
    }

    @Test
    public void should_create_valid_builds_list() {
        String buildListUrl = builder.getBuildList("bt297");
        assertEquals(TEAM_CITY_URL + "/app/rest/buildTypes/id:bt297/builds", buildListUrl);
    }

    @Test
    public void should_create_valid_version() {
        String versionUrl = builder.getVersion();
        assertEquals(TEAM_CITY_URL + "/app/rest/version", versionUrl);
    }

    @Test
    public void should_create_valid_server() {
        String serverUrl = builder.getServer();
        assertEquals(TEAM_CITY_URL + "/app/rest/server", serverUrl);
    }
}
