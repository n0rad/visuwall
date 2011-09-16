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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import net.awired.clients.common.Tests;
import net.awired.clients.teamcity.exception.TeamCityProjectsNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityAgent;
import net.awired.clients.teamcity.resource.TeamCityBuild;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityChange;
import net.awired.clients.teamcity.resource.TeamCityChanges;
import net.awired.clients.teamcity.resource.TeamCityProject;
import net.awired.clients.teamcity.resource.TeamCityProperty;
import net.awired.clients.teamcity.resource.TeamCityRelatedIssue;
import net.awired.clients.teamcity.resource.TeamCityRevision;
import net.awired.clients.teamcity.resource.TeamCityTag;
import net.awired.clients.teamcity.resource.TeamCityVcsRoot;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class TeamCityITest {

    private List<String> expectedProjectNames = Arrays.asList("mina", "zookeeper", "struts", "IT coverage");
    private int projectCount = expectedProjectNames.size();

    private static final String DATE_PATTERN = "\\d{8}T\\d{6}\\+\\d{4}";
    private static final String BUILD_TYPE_ID_PATTERN = "bt\\d*";
    private static final String PROJECT_ID_PATTERN = "project\\d*";
    private static final String BUILD_NUMBER_PATTERN = "\\d*";
    private static final String BUILD_ID_PATTERN = "\\d*";

    @Parameters
    public static Collection<Object[]> createParameters() {
        String instanceProperty = "teamcityInstances";
        return Tests.createUrlInstanceParametersFromProperty(instanceProperty);
    }

    TeamCity teamcity;

    public TeamCityITest(String teamcityUrl) {
        teamcity = new TeamCity(teamcityUrl);
    }

    @Test
    public void should_list_all_project_names() throws TeamCityProjectsNotFoundException {

        List<String> projectNames = teamcity.findProjectNames();
        assertEquals(expectedProjectNames.size(), projectNames.size());
        for (String expectedProjectName : expectedProjectNames) {
            assertTrue("teamcity must contain " + expectedProjectName, projectNames.contains(expectedProjectName));
        }
    }

    @Test
    public void should_find_all_project() throws TeamCityProjectsNotFoundException {
        List<TeamCityProject> projects = teamcity.findAllProjects();
        assertEquals(projectCount, projects.size());
        for (TeamCityProject project : projects) {
            String name = project.getName();
            assertTrue(expectedProjectNames.contains(name));
            assertNotNull(project.getId());
            assertNotNull(project.getHref());
        }
    }

    @Test
    public void should_load_struts_project() throws Exception {
        TeamCityProject project = findProject("struts");
        assertNotNull(project.getWebUrl());
        assertEquals("Struts project", project.getDescription());
        assertFalse(project.isArchived());
        assertEquals("struts", project.getName());
        assertNotNull(project.getId());
        assertNotNull(project.getHref());
    }

    @Test
    public void should_load_struts_build_types() throws Exception {
        TeamCityProject project = findProject("struts");

        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

        TeamCityBuildType buildType = teamCitybuildTypes.get(0);

        assertFalse(teamCitybuildTypes.isEmpty());
        assertNotNull(buildType.getId());
        assertEquals("struts-build-configuration", buildType.getName());
        assertNotNull(buildType.getHref());
        assertNotNull("struts", buildType.getProjectName());
        assertNotNull(buildType.getProjectId());
        assertNotNull(buildType.getWebUrl());
    }

    @Test
    public void should_load_last_struts_build() throws Exception {
        TeamCityBuild build = findLastBuild("struts");

        assertTrue(build.getId().matches("\\d*"));
        assertTrue(build.getNumber().matches("\\d*"));
        assertEquals("SUCCESS", build.getStatus());
        assertNotNull(build.getHref());
        assertNotNull(build.getWebUrl());
        assertFalse(build.isPersonal());
        assertFalse(build.isHistory());
        assertFalse(build.isPinned());
        assertTrue(build.getStartDate().matches(DATE_PATTERN));
        assertTrue(build.getFinishDate().matches(DATE_PATTERN));
        assertEquals("Tests passed: 331", build.getStatusText());

        TeamCityBuildType buildType = build.getBuildType();
        assertTrue(buildType.getId().matches(BUILD_TYPE_ID_PATTERN));
        assertEquals("struts-build-configuration", buildType.getName());
        assertNotNull(buildType.getHref());
        assertEquals("struts", buildType.getProjectName());
        assertTrue(buildType.getProjectId().matches(PROJECT_ID_PATTERN));
        assertNotNull(buildType.getWebUrl());

        TeamCityAgent agent = build.getAgent();
        assertEquals("Default Agent", agent.getName());

        List<TeamCityTag> tags = build.getTags();
        assertTrue(tags.isEmpty());

        List<TeamCityProperty> properties = build.getProperties();
        assertTrue(properties.isEmpty());

        List<TeamCityRevision> revisions = build.getRevisions();
        TeamCityRevision revision = revisions.get(0);
        assertNotNull(revision.getDisplayVersion());

        TeamCityVcsRoot vcsRoot = revision.getVcsRoot();
        assertNotNull(vcsRoot.getHref());
        assertNotNull(vcsRoot.getName());

        TeamCityChanges changes = build.getChanges();
        assertNotNull(changes.getHref());
        assertEquals(0, changes.getCount());

        List<TeamCityRelatedIssue> relatedIssues = build.getRelatedIssues();
        assertTrue(relatedIssues.isEmpty());
    }

    @Test
    public void should_load_build_type_with_builds() throws Exception {
        TeamCityProject project = findProject("struts");
        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();
        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds teamCityBuilds = teamcity.findBuildList(buildType.getId());

        assertTrue(teamCityBuilds.getCount() > 0);

        List<TeamCityBuildItem> buildList = teamCityBuilds.getBuilds();

        TeamCityBuildItem build = buildList.get(0);
        assertTrue(build.getId().matches(BUILD_ID_PATTERN));
        assertTrue(build.getNumber().matches(BUILD_NUMBER_PATTERN));
        assertEquals("SUCCESS", build.getStatus());
        assertTrue(build.getBuildTypeId().matches(BUILD_TYPE_ID_PATTERN));
        assertTrue(build.getStartDate().matches(DATE_PATTERN));
        assertNotNull(build.getHref());
        assertNotNull(build.getWebUrl());
    }

    @Test
    public void should_find_changes() throws Exception {
        TeamCityBuild build = findLastBuild("struts");

        List<TeamCityChange> findChanges = teamcity.findChanges(Integer.parseInt(build.getId()));
        assertTrue(findChanges.isEmpty());
    }

    @Test
    public void should_find_build_by_project_id_and_build_id() throws Exception {
        TeamCityProject project = findProject("struts");
        TeamCityBuild build = findLastBuild("struts");

        String projectId = project.getId();
        String buildNumber = build.getNumber();
        TeamCityBuild foundBuild = teamcity.findBuild(projectId, buildNumber);

        assertEquals(buildNumber, foundBuild.getNumber());
    }

    private TeamCityProject findProject(String projectName) throws Exception {
        for (TeamCityProject project : teamcity.findAllProjects()) {
            if (projectName.equals(project.getName())) {
                String id = project.getId();
                return teamcity.findProject(id);
            }
        }
        return null;
    }

    private TeamCityBuild findLastBuild(String projectName) throws Exception {
        TeamCityProject project = findProject(projectName);
        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds buildList = teamcity.findBuildList(buildType.getId());

        List<TeamCityBuildItem> builds = buildList.getBuilds();
        int lastBuild = builds.size() - 1;
        String buildId = builds.get(lastBuild).getId();

        TeamCityBuild build = teamcity.findBuild(Integer.parseInt(buildId));
        return build;
    }

}
