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

package net.awired.visuwall.server.domain;

import org.junit.Ignore;

@Ignore
public class WallIT {

    //    private Wall wall;
    //
    //    public WallIT() {
    //        wall = new Wall("test");
    //        List<SoftwareAccess> softwareAccesses = wall.getSoftwareAccesses();
    //        softwareAccesses.add(IntegrationTestData.HUDSON_ACCESS);
    //        wall.
    //    }
    //
    //    @Test
    //    public void should_retrieve_all_projects() {
    //        Collection<Project> projects = wall.getProjects();
    //        assertFalse(projects.isEmpty());
    //    }
    //
    //    @Test
    //    public void should_retrieve_state() throws ProjectNotFoundException {
    //        Project project = wall.findFreshProject("struts 2 instable");
    //        assertEquals(State.UNSTABLE, project.getState());
    //    }
    //
    //    @Test
    //    public void should_retrieve_data_of_one_project() throws ProjectNotFoundException {
    //        Project project = wall.findFreshProject("struts");
    //        assertEquals("struts", project.getName());
    //    }
    //
    //    @Test
    //    public void should_retrieve_quality_result() throws ProjectNotFoundException {
    //        Project project = wall.findFreshProject("struts");
    //        assertNotNull(project.getQualityResult());
    //        assertFalse(project.getQualityResult().getMeasures().isEmpty());
    //    }
    //
    //    @Test(expected=ProjectNotFoundException.class)
    //    public void should_throw_exception_when_search_non_existant_project() throws ProjectNotFoundException {
    //        wall.findFreshProject("does.not.exist");
    //    }
    //
    //    @Test
    //    public void should_retrieve_project_with_no_last_build() throws ProjectNotFoundException {
    //        Project project = wall.findFreshProject("neverbuild");
    //        assertNull(project.getCompletedBuild());
    //    }
    //
    //    @Test
    //    public void should_retrieve_estimated_finish_time_of_not_building_project() throws ProjectNotFoundException {
    //        Date estimatedFinishTime = wall.getEstimatedFinishTime("struts");
    //        assertNotNull(estimatedFinishTime);
    //    }
    //
    //    @Test
    //    public void should_retrieve_test_result() throws BuildNotFoundException {
    //        Build build = wall.findBuildByBuildNumber("struts", 3);
    //        TestResult testResult = build.getTestResult();
    //        assertEquals(0, testResult.getFailCount());
    //        assertEquals(0, testResult.getSkipCount());
    //        assertEquals(331, testResult.getTotalCount());
    //        assertEquals(0, testResult.getIntegrationTestCount());
    //    }
    //
    //    @Test
    //    public void should_retrieve_status() {
    //        Collection<Project> projects = wall.getProjects();
    //        List<ProjectStatus> status = wall.getStatus();
    //
    //        assertEquals(projects.size(), status.size());
    //
    //        for (ProjectStatus stat:status) {
    //            assertFalse(StringUtils.isBlank(stat.getName()));
    //            assertNotNull(stat.getState());
    //        }
    //    }
    //
    //    @Ignore
    //    @Test
    //    public void should_find_projects_with_spaces_in_name() throws ProjectNotFoundException {
    //        Wall bambooWall = new Wall("bambooWall");
    //        bambooWall.addSoftwareAccess(new SoftwareAccess(Software.BAMBOO, BAMBOO_URL));
    //        bambooWall.discoverProjects();
    //
    //        Collection<Project> projects = bambooWall.getProjects();
    //        assertFalse(projects.isEmpty());
    //        for (Project project:projects) {
    //            System.err.println(project);
    //            Project freshProject = bambooWall.findFreshProject(project.getName());
    //        }
    //    }

}
