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

package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.plugin.jenkins.JenkinsConnection.JENKINS_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class JenkinsConnectionTest {

    Hudson hudson;
    JenkinsConnection jenkinsPlugin;

    @Before
    public void init() {
        hudson = Mockito.mock(Hudson.class);
        jenkinsPlugin = new JenkinsConnection();
        jenkinsPlugin.connect("url");
        jenkinsPlugin.hudson = hudson;
    }

    @Test
    public void should_return_state_unknow_if_no_state() throws ProjectNotFoundException,
            HudsonJobNotFoundException {
        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("not_valid_state");

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "name");
		State state = jenkinsPlugin.getLastBuildState(projectId);
        assertEquals(State.UNKNOWN, state);
    }

    @Test
    public void should_return_state_valid_state() throws ProjectNotFoundException, HudsonJobNotFoundException {
        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("FAILURE");

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "name");
        State state = jenkinsPlugin.getLastBuildState(projectId);
        assertEquals(State.FAILURE, state);
    }

    @Test
    public void should_find_all_projects_from_hudson() {
        List<HudsonProject> hudsonProjects = new ArrayList<HudsonProject>();
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setArtifactId("artifactId");
        hudsonProject.setName("name");

        hudsonProjects.add(hudsonProject);

        when(hudson.findAllProjects()).thenReturn(hudsonProjects);

        List<ProjectId> projectIds = jenkinsPlugin.findAllProjects();
        ProjectId projectId = projectIds.get(0);

        assertEquals("artifactId", projectId.getArtifactId());
        assertEquals("name", projectId.getId(JENKINS_ID));
        assertEquals("name", projectId.getName());
    }

    @Test
    public void should_find_project_by_projectId() throws HudsonJobNotFoundException, ProjectNotFoundException {
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("name");
        when(hudson.findProject(Matchers.anyString())).thenReturn(hudsonProject);

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "id");

        Project project = jenkinsPlugin.findProject(projectId);

        assertEquals(hudsonProject.getName(), project.getName());
    }

    @Test
    public void should_get_is_building_information() throws HudsonJobNotFoundException, ProjectNotFoundException {

        when(hudson.isBuilding("project1")).thenReturn(true);
        when(hudson.isBuilding("project2")).thenReturn(false);

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "project1");

        assertTrue(jenkinsPlugin.isBuilding(projectId));

        projectId.addId(JENKINS_ID, "project2");
        assertFalse(jenkinsPlugin.isBuilding(projectId));
    }

    @Test
    public void should_get_estimated_finish_time() throws HudsonJobNotFoundException, ProjectNotFoundException {
        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "project1");

        assertEquals(date, jenkinsPlugin.getEstimatedFinishTime(projectId));
    }

    @Test
    public void should_find_build_by_number() throws BuildNotFoundException, ProjectNotFoundException,
            HudsonBuildNotFoundException, HudsonJobNotFoundException {
        HudsonBuild hudsonBuild = new HudsonBuild();
		hudsonBuild.setState(JenkinsState.SUCCESS);

        when(hudson.findBuild("project1", 0)).thenReturn(hudsonBuild);

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "project1");

        Build build = jenkinsPlugin.findBuildByBuildNumber(projectId, 0);

        assertNotNull(build);
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        ProjectId projectId = new ProjectId();
        projectId.addId(JENKINS_ID, "project1");

        int lastBuildNumber = jenkinsPlugin.getLastBuildNumber(projectId);

        assertEquals(5, lastBuildNumber);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url() {
        new JenkinsConnection().connect("");
    }

    @Test
    public void should_create_hudson() {
        new JenkinsConnection().connect("url");
    }

    @Test
    public void should_get_description() throws Exception {
        when(hudson.getDescription("projectName")).thenReturn("description");

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectName");

        String description = jenkinsPlugin.getDescription(softwareProjectId);

        assertEquals("description", description);
    }

}
