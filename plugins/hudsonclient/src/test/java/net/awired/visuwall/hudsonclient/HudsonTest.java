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

package net.awired.visuwall.hudsonclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonJob;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;
import net.awired.visuwall.hudsonclient.finder.HudsonFinder;
import net.awired.visuwall.hudsonclient.finder.HudsonRootModuleFinder;
import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;

public class HudsonTest {

    GenericSoftwareClient client;
    HudsonFinder hudsonFinder;
    HudsonUrlBuilder hudsonUrlBuilder;
    HudsonRootModuleFinder hudsonRootModuleFinder;
    Hudson hudson;

    @Before
    public void init() {
        client = mock(GenericSoftwareClient.class);
        hudsonFinder = mock(HudsonFinder.class);
        hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        hudsonRootModuleFinder = mock(HudsonRootModuleFinder.class);
        hudson = new Hudson("http://hudson.com");
        hudson.hudsonFinder = hudsonFinder;
        hudson.hudsonRootModuleFinder = hudsonRootModuleFinder;
    }

    @Test
    public void should_find_all_projects() throws Exception {
        when(hudsonFinder.findJobNames()).thenReturn(Arrays.asList("project1"));
        when(hudsonFinder.findJob("project1")).thenReturn(new HudsonJob());
        when(hudsonRootModuleFinder.findArtifactId(anyString())).thenReturn("artifactId");

        List<HudsonJob> projects = hudson.findAllProjects();

        assertEquals(1, projects.size());
    }

    @Test
    public void should_not_fail_if_there_is_hudson_project_name_not_found_exception()
            throws HudsonJobNotFoundException {
        when(hudsonFinder.findJobNames()).thenReturn(Arrays.asList("project1"));
        when(hudsonFinder.findJob("project1")).thenThrow(new HudsonJobNotFoundException("cause"));
        hudson.findAllProjects();
    }

    @Test
    public void should_find_build() throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        when(hudsonFinder.find(anyString(), anyInt())).thenReturn(new HudsonBuild());

        HudsonBuild build = hudson.findBuild("projectName", 5);

        assertNotNull(build);
    }

    @Test
    public void should_get_last_build_number() throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        when(hudsonFinder.getLastBuildNumber("projectName")).thenReturn(42);

        int lastBuildNumber = hudson.getLastBuildNumber("projectName");

        assertEquals(42, lastBuildNumber);
    }

    @Test
    public void should_get_estimated_finish_time() throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        HudsonBuild build = new HudsonBuild();
        build.setDuration(Minutes.TWO.toStandardDuration().getMillis());
        build.setSuccessful(true);
        build.setStartTime(new Date());
        when(hudsonFinder.getCurrentBuild("projectName")).thenReturn(build);
        when(hudsonFinder.find("projectName", 1)).thenReturn(build);

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName("projectName");
        when(hudsonFinder.findJob("projectName")).thenReturn(hudsonJob);

        when(hudsonFinder.getBuildNumbers("projectName")).thenReturn(Arrays.asList(1));

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertTrue(date.after(new Date()));
    }

    @Test
    public void should_get_estimated_finish_time_even_if_there_is_no_current_build()
            throws HudsonJobNotFoundException {
        HudsonJob hudsonProject = new HudsonJob();
        hudsonProject.setName("projectName");
        when(hudsonFinder.findJob("projectName")).thenReturn(hudsonProject);

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertNotNull(date);
    }

    @Test
    public void should_get_estimated_finish_time_even_if_there_is_no_start_time() throws HudsonJobNotFoundException,
            HudsonBuildNotFoundException {
        HudsonBuild build = new HudsonBuild();
        build.setDuration(Minutes.TWO.toStandardDuration().getMillis());
        build.setSuccessful(true);
        build.setStartTime(null);

        HudsonJob hudsonProject = new HudsonJob();
        hudsonProject.setName("projectName");

        when(hudsonFinder.findJob("projectName")).thenReturn(hudsonProject);
        when(hudsonFinder.find("projectName", 1)).thenReturn(build);

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertNotNull(date);
    }

    @Test
    public void should_get_is_building() throws HudsonJobNotFoundException {
        when(hudsonFinder.isBuilding("projectName")).thenReturn(true);

        boolean isBuilding = hudson.isBuilding("projectName");

        assertTrue(isBuilding);
    }

    @Test
    public void should_get_project_names() {
        List<String> projectNames = new ArrayList<String>();
        projectNames.add("project1");
        projectNames.add("project2");

        when(hudsonFinder.findJobNames()).thenReturn(projectNames);

        List<String> names = hudson.findJobNames();

        assertEquals("project1", names.get(0));
        assertEquals("project2", names.get(1));
    }

    @Test
    public void should_get_description() throws HudsonJobNotFoundException {
        when(hudsonFinder.getDescription("projectName")).thenReturn("description");

        String description = hudson.getDescription("projectName");

        assertEquals("description", description);
    }
}
