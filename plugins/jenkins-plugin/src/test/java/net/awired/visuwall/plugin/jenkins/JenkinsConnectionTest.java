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
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonJob;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class JenkinsConnectionTest {

    @Mock
    Hudson hudson;

    JenkinsConnection jenkinsConnection;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        jenkinsConnection = new JenkinsConnection();
        jenkinsConnection.connect("http://jenkins:8080");
        jenkinsConnection.hudson = hudson;
    }

    @Test
    public void should_find_all_projects_from_hudson() {
        List<HudsonJob> hudsonProjects = new ArrayList<HudsonJob>();
        HudsonJob hudsonProject = new HudsonJob();
        hudsonProject.setName("name");

        hudsonProjects.add(hudsonProject);

        when(hudson.findAllProjects()).thenReturn(hudsonProjects);

        List<SoftwareProjectId> projectIds = jenkinsConnection.findAllSoftwareProjectIds();
        SoftwareProjectId projectId = projectIds.get(0);

        assertEquals("name", projectId.getProjectId());
    }

    @Test
    public void should_get_is_building_information() throws Exception {

        when(hudson.isBuilding("project1")).thenReturn(true);
        when(hudson.isBuilding("project2")).thenReturn(false);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertTrue(jenkinsConnection.isBuilding(projectId, 0));

        projectId = new SoftwareProjectId("project2");
        assertFalse(jenkinsConnection.isBuilding(projectId, 0));
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertEquals(date, jenkinsConnection.getEstimatedFinishTime(projectId, 0));
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        int lastBuildNumber = jenkinsConnection.getLastBuildNumber(projectId);

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

        String description = jenkinsConnection.getDescription(softwareProjectId);

        assertEquals("description", description);
    }

}
