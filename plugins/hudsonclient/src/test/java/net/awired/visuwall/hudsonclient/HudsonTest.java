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
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.finder.HudsonFinder;

import org.joda.time.Minutes;
import org.junit.Before;
import org.junit.Test;

public class HudsonTest {

    GenericSoftwareClient client;
    HudsonFinder hudsonFinder;
    HudsonUrlBuilder hudsonUrlBuilder;
    Hudson hudson;

    @Before
    public void init() {
        client = mock(GenericSoftwareClient.class);
        hudsonFinder = mock(HudsonFinder.class);
        hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        hudson = new Hudson(hudsonFinder);
    }

    @Test
    public void should_find_all_projects() throws HudsonProjectNotFoundException {
        when(hudsonFinder.findProjectNames()).thenReturn(Arrays.asList("project1"));
        when(hudsonFinder.findProject("project1")).thenReturn(new HudsonProject());

        List<HudsonProject> projects = hudson.findAllProjects();

        assertEquals(1, projects.size());
    }

    @Test
    public void should_not_fail_if_there_is_hudson_project_name_not_found_exception()
            throws HudsonProjectNotFoundException {
        when(hudsonFinder.findProjectNames()).thenReturn(Arrays.asList("project1"));
        when(hudsonFinder.findProject("project1")).thenThrow(new HudsonProjectNotFoundException("cause"));
        hudson.findAllProjects();
    }

    @Test
    public void should_find_build() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        when(hudsonFinder.find(anyString(), anyInt())).thenReturn(new HudsonBuild());

        HudsonBuild build = hudson.findBuild("projectName", 5);

        assertNotNull(build);
    }

    @Test
    public void should_build_average_build_duration_time() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("projectName");
        hudsonProject.setBuildNumbers(new int[] { 1, 2 });

        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);
        HudsonBuild build1 = new HudsonBuild();
        build1.setDuration(4);
        build1.setSuccessful(true);

        HudsonBuild build2 = new HudsonBuild();
        build2.setDuration(2);
        build2.setSuccessful(true);

        when(hudsonFinder.find("projectName", 1)).thenReturn(build1);
        when(hudsonFinder.find("projectName", 2)).thenReturn(build2);

        long averageBuildDurationTime = hudson.getAverageBuildDurationTime("projectName");

        assertEquals(3, averageBuildDurationTime);
    }

    @Test
    public void should_build_max_build_duration_time() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("projectName");
        hudsonProject.setBuildNumbers(new int[] { 1, 2 });

        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);
        HudsonBuild build1 = new HudsonBuild();
        build1.setDuration(4);
        build1.setSuccessful(false);

        HudsonBuild build2 = new HudsonBuild();
        build2.setDuration(2);
        build2.setSuccessful(false);

        when(hudsonFinder.find("projectName", 1)).thenReturn(build1);
        when(hudsonFinder.find("projectName", 2)).thenReturn(build2);

        long averageBuildDurationTime = hudson.getAverageBuildDurationTime("projectName");

        assertEquals(4, averageBuildDurationTime);
    }

    @Test
    public void should_get_last_build_number() throws HudsonProjectNotFoundException, HudsonBuildNotFoundException {
        when(hudsonFinder.getLastBuildNumber("projectName")).thenReturn(42);

        int lastBuildNumber = hudson.getLastBuildNumber("projectName");

        assertEquals(42, lastBuildNumber);
    }

    @Test
    public void should_get_valid_state() throws HudsonProjectNotFoundException, HudsonBuildNotFoundException {
        when(hudsonFinder.getLastBuildNumber("projectName")).thenReturn(42);
        when(hudsonFinder.getStateOf("projectName", 42)).thenReturn("SUCCESS");

        String state = hudson.getState("projectName");

        assertEquals("SUCCESS", state);
    }

    @Test
    public void should_get_unstable_state_if_tests_pass() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        HudsonTestResult unitTests = new HudsonTestResult();
        unitTests.setPassCount(1);
        HudsonTestResult integrationTests = new HudsonTestResult();
        integrationTests.setPassCount(1);

        HudsonBuild completedBuild = new HudsonBuild();
        completedBuild.setUnitTestResult(unitTests);
        completedBuild.setIntegrationTestResult(integrationTests);

        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setCompletedBuild(completedBuild);

        when(hudsonFinder.getLastBuildNumber("projectName")).thenReturn(42);
        when(hudsonFinder.getStateOf("projectName", 42)).thenReturn("FAILURE");
        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);

        String state = hudson.getState("projectName");

        assertEquals("UNSTABLE", state);
    }

    @Test
    public void should_get_failure_state_if_tests_dont_pass() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        HudsonProject hudsonProject = new HudsonProject();

        when(hudsonFinder.getLastBuildNumber("projectName")).thenReturn(42);
        when(hudsonFinder.getStateOf("projectName", 42)).thenReturn("FAILURE");
        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);

        String state = hudson.getState("projectName");

        assertEquals("FAILURE", state);
    }

    @Test
    public void should_get_new_state_if_build_is_not_found() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        when(hudsonFinder.getLastBuildNumber("projectName")).thenThrow(new HudsonBuildNotFoundException("cause"));

        String state = hudson.getState("projectName");

        assertEquals("UNKNOWN", state);
    }

    @Test
    public void should_get_estimated_finish_time() throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        HudsonBuild build = new HudsonBuild();
        build.setDuration(Minutes.TWO.toStandardDuration().getMillis());
        build.setSuccessful(true);
        build.setStartTime(new Date());

        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("projectName");
        hudsonProject.setCurrentBuild(build);
        hudsonProject.setBuildNumbers(new int[] { 1 });

        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);
        when(hudsonFinder.find("projectName", 1)).thenReturn(build);

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertTrue(date.after(new Date()));
    }

    @Test
    public void should_get_estimated_finish_time_even_if_there_is_no_current_build()
            throws HudsonProjectNotFoundException {
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("projectName");
        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertNotNull(date);
    }

    @Test
    public void should_get_estimated_finish_time_even_if_there_is_no_start_time()
            throws HudsonProjectNotFoundException, HudsonBuildNotFoundException {
        HudsonBuild build = new HudsonBuild();
        build.setDuration(Minutes.TWO.toStandardDuration().getMillis());
        build.setSuccessful(true);
        build.setStartTime(null);

        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("projectName");
        hudsonProject.setCurrentBuild(build);
        hudsonProject.setBuildNumbers(new int[] { 1 });

        when(hudsonFinder.findProject("projectName")).thenReturn(hudsonProject);
        when(hudsonFinder.find("projectName", 1)).thenReturn(build);

        Date date = hudson.getEstimatedFinishTime("projectName");

        assertNotNull(date);
    }

    @Test
    public void should_get_is_building() throws HudsonProjectNotFoundException {
        when(hudsonFinder.isBuilding("projectName")).thenReturn(true);

        boolean isBuilding = hudson.isBuilding("projectName");

        assertTrue(isBuilding);
    }

    @Test
    public void should_get_project_names() {
        List<String> projectNames = new ArrayList<String>();
        projectNames.add("project1");
        projectNames.add("project2");

        when(hudsonFinder.findProjectNames()).thenReturn(projectNames);

        List<String> names = hudson.findProjectNames();

        assertEquals("project1", names.get(0));
        assertEquals("project2", names.get(1));
    }
}
