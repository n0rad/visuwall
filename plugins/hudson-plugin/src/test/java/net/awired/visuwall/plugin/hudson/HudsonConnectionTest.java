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
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.awired.clients.hudson.Hudson;
import net.awired.clients.hudson.domain.HudsonJob;
import net.awired.clients.hudson.exception.HudsonJobNotFoundException;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HudsonConnectionTest {

    @Mock
    Hudson hudson;

    HudsonConnection hudsonConnection;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        hudsonConnection = new HudsonConnection();
        hudsonConnection.connect("http://hudson:8080");
        hudsonConnection.hudson = hudson;
    }

    @Test
    public void should_find_all_projects_from_hudson() {
        List<HudsonJob> hudsonProjects = new ArrayList<HudsonJob>();
        HudsonJob hudsonProject = new HudsonJob();
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
    public void should_get_is_building_information() throws Exception {
        when(hudson.isBuilding("project1")).thenReturn(true);
        when(hudson.isBuilding("project2")).thenReturn(false);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertTrue(hudsonConnection.isBuilding(projectId, 0));

        projectId = new SoftwareProjectId("project2");
        assertFalse(hudsonConnection.isBuilding(projectId, 0));
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertEquals(date, hudsonConnection.getEstimatedFinishTime(projectId, 0));
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        int lastBuildNumber = hudsonConnection.getLastBuildNumber(projectId);

        assertEquals(5, lastBuildNumber);
    }

    @Test
    public void should_get_a_disabled_project() throws Exception {
        HudsonJob job = new HudsonJob();
        job.setDisabled(true);

        when(hudson.findJob(anyString())).thenReturn(job);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = hudsonConnection.isProjectDisabled(softwareProjectId);

        assertTrue(isDisabled);
    }

    @Test
    public void should_get_an_enabled_project() throws Exception {
        HudsonJob job = new HudsonJob();
        job.setDisabled(false);

        when(hudson.findJob(anyString())).thenReturn(job);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = hudsonConnection.isProjectDisabled(softwareProjectId);

        assertFalse(isDisabled);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Throwable notFound = new HudsonJobNotFoundException("not found");
        when(hudson.findJob(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        hudsonConnection.isProjectDisabled(softwareProjectId);
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
