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
import java.util.Collection;
import java.util.List;
import net.awired.clients.common.Tests;
import net.awired.clients.teamcity.exception.TeamCityBuildNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityProjectsNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityAgent;
import net.awired.clients.teamcity.resource.TeamCityBuild;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityChange;
import net.awired.clients.teamcity.resource.TeamCityChanges;
import net.awired.clients.teamcity.resource.TeamCityProject;
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

    @Parameters
    public static Collection<Object[]> createParameters() {
        String instanceProperty = "teamcityInstances";
        return Tests.createUrlInstanceParametersFromProperty(instanceProperty);
    }

    TeamCity teamcity;

    public TeamCityITest(String teamcityUrl) {
        teamcity = new TeamCity(teamcityUrl);
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_parameter_in_constructor() {
        new TeamCity(null);
    }

    @Test
    public void should_list_all_project_names() throws TeamCityProjectsNotFoundException {
        List<String> projectNames = teamcity.findProjectNames();
        assertFalse(projectNames.isEmpty());
        for (String s : projectNames) {
            assertNotNull(s);
        }
    }

    @Test
    public void should_find_all_project() throws TeamCityProjectsNotFoundException {
        List<TeamCityProject> projects = teamcity.findAllProjects();
        assertFalse(projects.isEmpty());
        for (TeamCityProject project : projects) {
            assertNotNull(project.getName());
            assertNotNull(project.getId());
            assertNotNull(project.getHref());
        }
    }

    @Test
    public void should_load_project() throws Exception {
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());
        assertNotNull(project.getWebUrl());
        assertNotNull(project.getDescription());
        assertNotNull(project.isArchived());
        assertNotNull(project.getName());
        assertNotNull(project.getId());
        assertNotNull(project.getHref());
    }

    @Test
    public void should_load_project_with_build_types() throws Exception {
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());

        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

        TeamCityBuildType buildType = teamCitybuildTypes.get(0);

        assertFalse(teamCitybuildTypes.isEmpty());
        assertNotNull(buildType.getId());
        assertNotNull(buildType.getName());
        assertNotNull(buildType.getHref());
        assertNotNull(buildType.getProjectName());
        assertNotNull(buildType.getProjectId());
        assertNotNull(buildType.getWebUrl());
    }

    @Test
    public void should_load_build() throws Exception {
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());

        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds buildList = teamcity.findBuildList(buildType.getId());

        String buildId = buildList.getBuilds().get(0).getId();

        TeamCityBuild build = teamcity.findBuild(Integer.parseInt(buildId));

        assertNotNull(build.getId());
        assertNotNull(build.getNumber());
        assertNotNull(build.getStatus());
        assertNotNull(build.getHref());
        assertNotNull(build.getWebUrl());
        assertFalse(build.isPersonal());
        assertFalse(build.isHistory());
        assertFalse(build.isPinned());
        assertNotNull(build.getStartDate());
        assertNotNull(build.getFinishDate());
        assertNotNull(build.getStatusText());

        buildType = build.getBuildType();
        assertNotNull(buildType.getId());
        assertNotNull(buildType.getName());
        assertNotNull(buildType.getHref());
        assertNotNull(buildType.getProjectName());
        assertNotNull(buildType.getProjectId());
        assertNotNull(buildType.getWebUrl());

        TeamCityAgent agent = build.getAgent();
        assertNotNull(agent.getName());

        List<TeamCityTag> tags = build.getTags();
        assertTrue(tags.isEmpty());

        // TODO: add property
        //        List<TeamCityProperty> properties = build.getProperties();
        //        TeamCityProperty property = properties.get(0);
        //        assertEquals("system.rel.version", property.getName());
        //        assertEquals("1.7.x", property.getValue());

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
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());
        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();
        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds teamCityBuilds = teamcity.findBuildList(buildType.getId());

        //        assertNotNull(teamCityBuilds.getNextHref());
        assertTrue(teamCityBuilds.getCount() > 0);

        List<TeamCityBuildItem> buildList = teamCityBuilds.getBuilds();

        TeamCityBuildItem build = buildList.get(0);
        assertNotNull(build.getId());
        assertNotNull(build.getNumber());
        assertNotNull(build.getStatus());
        assertNotNull(build.getBuildTypeId());
        assertNotNull(build.getStartDate());
        assertNotNull(build.getHref());
        assertNotNull(build.getWebUrl());
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_pass_negative_number_to_find_build_method() throws TeamCityBuildNotFoundException {
        TeamCity teamcity = new TeamCity();
        teamcity.findBuild(-1);
    }

    @Test
    public void should_find_changes() throws Exception {
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());

        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds buildList = teamcity.findBuildList(buildType.getId());

        String buildId = buildList.getBuilds().get(0).getId();

        TeamCityBuild build = teamcity.findBuild(Integer.parseInt(buildId));

        List<TeamCityChange> findChanges = teamcity.findChanges(Integer.parseInt(build.getId()));
        assertTrue(findChanges.isEmpty());
    }

    @Test
    public void should_find_build() throws Exception {
        TeamCityProject project = teamcity.findAllProjects().get(0);
        project = teamcity.findProject(project.getId());

        String projectId = project.getId();

        List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();
        TeamCityBuildType buildType = teamCitybuildTypes.get(0);
        TeamCityBuilds teamCityBuilds = teamcity.findBuildList(buildType.getId());
        List<TeamCityBuildItem> buildList = teamCityBuilds.getBuilds();
        TeamCityBuildItem build = buildList.get(0);

        String buildNumber = build.getNumber();
        TeamCityBuild teamcityBuild = teamcity.findBuild(projectId, buildNumber);

        assertBuildIsOk(teamcityBuild);
    }

    private void assertBuildIsOk(TeamCityBuild teamcityBuild) {
        assertNotNull(teamcityBuild.getFinishDate());
        assertNotNull(teamcityBuild.getHref());
        assertNotNull(teamcityBuild.getId());
        assertNotNull(teamcityBuild.getNumber());
        assertNotNull(teamcityBuild.getProperties());
        assertNotNull(teamcityBuild.getRelatedIssues());
        assertNotNull(teamcityBuild.getRevisions());
        assertNotNull(teamcityBuild.getStartDate());
        assertNotNull(teamcityBuild.getStatus());
        assertNotNull(teamcityBuild.getStatusText());
        assertNotNull(teamcityBuild.getTags());
        //assertNotNull(teamcityBuild.getVcsRoot());
        assertNotNull(teamcityBuild.getWebUrl());

        TeamCityAgent agent = teamcityBuild.getAgent();
        assertAgentIsOk(agent);

        TeamCityBuildType buildType = teamcityBuild.getBuildType();
        assertBuildTypeIsOk(buildType);

        TeamCityChanges changes = teamcityBuild.getChanges();
        assertChangesIsOk(changes);
    }

    private void assertChangesIsOk(TeamCityChanges changes) {
        assertNotNull(changes);
        assertNotNull(changes.getChanges());
    }

    private void assertAgentIsOk(TeamCityAgent agent) {
        assertNotNull(agent);
        assertNotNull(agent.getName());
    }

    private void assertBuildTypeIsOk(TeamCityBuildType buildType2) {
        assertNotNull(buildType2);
        assertNotNull(buildType2.getHref());
        assertNotNull(buildType2.getId());
        assertNotNull(buildType2.getName());
        assertNotNull(buildType2.getProjectId());
        assertNotNull(buildType2.getProjectName());
        assertNotNull(buildType2.getWebUrl());
    }

}
