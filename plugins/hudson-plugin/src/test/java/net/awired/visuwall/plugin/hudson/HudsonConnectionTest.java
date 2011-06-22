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

package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class HudsonConnectionTest {

    @Test
    public void should_return_state_unknow_if_no_state() throws ProjectNotFoundException,
            HudsonJobNotFoundException {
        Hudson hudson = Mockito.mock(Hudson.class);

        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("not_valid_state");
        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        SoftwareProjectId projectId = new SoftwareProjectId("idValue");
        State state = hudsonPlugin.getLastBuildState(projectId);
        assertEquals(State.UNKNOWN, state);
    }

    @Test
    public void should_return_state_valid_state() throws ProjectNotFoundException, HudsonJobNotFoundException {
        Hudson hudson = Mockito.mock(Hudson.class);

        Mockito.when(hudson.getState(Matchers.anyString())).thenReturn("FAILURE");

        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        SoftwareProjectId projectId = new SoftwareProjectId("idValue");
        State state = hudsonPlugin.getLastBuildState(projectId);
        assertEquals(State.FAILURE, state);
    }

    @Test
    public void should_find_all_projects_from_hudson() {
        Hudson hudson = Mockito.mock(Hudson.class);
        List<HudsonProject> hudsonProjects = new ArrayList<HudsonProject>();
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("name");

        hudsonProjects.add(hudsonProject);

        when(hudson.findAllProjects()).thenReturn(hudsonProjects);

        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        List<SoftwareProjectId> projectIds = hudsonPlugin.findAllSoftwareProjectIds();
        SoftwareProjectId projectId = projectIds.get(0);

        assertEquals("name", projectId.getProjectId());
    }

    @Test
    public void should_get_is_building_information() throws HudsonJobNotFoundException, ProjectNotFoundException {
        Hudson hudson = Mockito.mock(Hudson.class);

        when(hudson.isBuilding("project1")).thenReturn(true);
        when(hudson.isBuilding("project2")).thenReturn(false);

        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertTrue(hudsonPlugin.isBuilding(projectId));

        projectId = new SoftwareProjectId("project2");
        assertFalse(hudsonPlugin.isBuilding(projectId));
    }

    @Test
    public void should_get_estimated_finish_time() throws HudsonJobNotFoundException, ProjectNotFoundException {
        Hudson hudson = Mockito.mock(Hudson.class);

        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertEquals(date, hudsonPlugin.getEstimatedFinishTime(projectId));
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        Hudson hudson = Mockito.mock(Hudson.class);

        HudsonConnection hudsonPlugin = new HudsonConnection();
        hudsonPlugin.connect("url");
        hudsonPlugin.hudson = hudson;

        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        int lastBuildNumber = hudsonPlugin.getLastBuildNumber(projectId);

        assertEquals(5, lastBuildNumber);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url() {
        new HudsonConnection().connect("");
    }

    @Test
    public void should_create_hudson() {
        new HudsonConnection().connect("url");
    }

}
