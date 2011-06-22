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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
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
    public void should_return_state_unknow_if_no_state() throws Exception,
            HudsonJobNotFoundException {
        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("not_valid_state");

        SoftwareProjectId projectId = new SoftwareProjectId("name");
        State state = jenkinsPlugin.getBuildState(projectId, 0);
        assertEquals(State.UNKNOWN, state);
    }

    @Test
    public void should_return_state_valid_state() throws Exception {
        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("FAILURE");

        SoftwareProjectId projectId = new SoftwareProjectId("name");
        State state = jenkinsPlugin.getBuildState(projectId, 0);
        assertEquals(State.FAILURE, state);
    }

    @Test
    public void should_find_all_projects_from_hudson() {
        List<HudsonProject> hudsonProjects = new ArrayList<HudsonProject>();
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("name");

        hudsonProjects.add(hudsonProject);

        when(hudson.findAllProjects()).thenReturn(hudsonProjects);

        List<SoftwareProjectId> projectIds = jenkinsPlugin.findAllSoftwareProjectIds();
        SoftwareProjectId projectId = projectIds.get(0);

        assertEquals("name", projectId.getProjectId());
    }

    @Test
    public void should_get_is_building_information() throws Exception {

        when(hudson.isBuilding("project1")).thenReturn(true);
        when(hudson.isBuilding("project2")).thenReturn(false);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertTrue(jenkinsPlugin.isBuilding(projectId, 0));

        projectId = new SoftwareProjectId("project2");
        assertFalse(jenkinsPlugin.isBuilding(projectId, 0));
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertEquals(date, jenkinsPlugin.getEstimatedFinishTime(projectId, 0));
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

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
