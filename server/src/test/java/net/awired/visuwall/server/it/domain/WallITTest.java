/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.server.it.domain;

import static net.awired.visuwall.server.IntegrationTestData.HUDSON_URL;
import static net.awired.visuwall.server.IntegrationTestData.SONAR_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;

import org.apache.commons.lang.StringUtils;
import org.junit.Test;


public class WallITTest {

    private Wall wall;

    public WallITTest() {
        wall = new Wall("test");
        wall.addSoftwareAccess(new SoftwareAccess(Software.HUDSON, HUDSON_URL));
        wall.addSoftwareAccess(new SoftwareAccess(Software.SONAR, SONAR_URL));
        wall.refreshProjects();
    }

    @Test
    public void should_retrieve_all_projects() {
        Collection<Project> projects = wall.getProjects();
        for (Project project : projects) {
            System.err.println(project);
        }
    }

    @Test
    public void should_retrieve_state() throws ProjectNotFoundException {
        Project project = wall.findProjectByName("struts 2 instable");
        assertEquals(State.UNSTABLE, project.getState());
    }

    @Test
    public void should_retrieve_data_of_one_project() throws ProjectNotFoundException {
        Project project = wall.findProjectByName("struts");
        assertEquals("struts", project.getName());
    }

    @Test
    public void should_retrieve_quality_result() throws ProjectNotFoundException {
        Project project = wall.findProjectByName("struts");
        assertNotNull(project.getQualityResult());
        assertFalse(project.getQualityResult().getMeasures().isEmpty());
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_search_non_existant_project() throws ProjectNotFoundException {
        wall.findProjectByName("does.not.exist");
    }

    @Test
    public void should_retrieve_project_with_no_last_build() throws ProjectNotFoundException {
        Project project = wall.findProjectByName("neverbuild");
        assertNull(project.getCompletedBuild());
    }

    @Test
    public void should_retrieve_estimated_finish_time_of_not_building_project() throws ProjectNotFoundException {
        Date estimatedFinishTime = wall.getEstimatedFinishTime("struts");
        assertNotNull(estimatedFinishTime);
    }

    @Test
    public void should_retrieve_test_result() throws BuildNotFoundException {
        Build build = wall.findBuildByProjectNameAndBuilderNumber("struts", 3);
        TestResult testResult = build.getTestResult();
        assertEquals(0, testResult.getFailCount());
        assertEquals(0, testResult.getSkipCount());
        assertEquals(331, testResult.getTotalCount());
        assertEquals(0, testResult.getIntegrationTestCount());
    }

    @Test
    public void should_retrieve_status() {
        Collection<Project> projects = wall.getProjects();
        List<ProjectStatus> status = wall.getStatus();

        assertEquals(projects.size(), status.size());

        for (ProjectStatus stat:status) {
            assertFalse(StringUtils.isBlank(stat.getName()));
            assertNotNull(stat.getState());
        }
    }

}
