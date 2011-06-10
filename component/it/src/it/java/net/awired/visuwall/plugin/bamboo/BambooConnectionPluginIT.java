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

package net.awired.visuwall.plugin.bamboo;

import static net.awired.visuwall.IntegrationTestData.BAMBOO_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.joda.time.DateTime;
import org.junit.Ignore;
import org.junit.Test;

public class BambooConnectionPluginIT {

    private static final String BAMBOO_ID = "BAMBOO_ID";

    static BambooConnectionPlugin bambooPlugin = new BambooConnectionPlugin(BAMBOO_URL);

    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = bambooPlugin.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_project() throws ProjectNotFoundException {
        ProjectId projectId = struts2ProjectId();
        Project project = bambooPlugin.findProject(projectId);
        assertNotNull(project);
    }

    @Test
    public void should_find_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
        ProjectId projectId = strutsProjectId();
        int buildNumber = bambooPlugin.getLastBuildNumber(projectId);
        assertEquals(3, buildNumber);
    }

    @Test
    public void should_find_build_by_name_and_build_number() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        Build build = bambooPlugin.findBuildByBuildNumber(projectId, 3);
        assertNotNull(build);
        assertEquals(3, build.getBuildNumber());
        assertEquals(30181, build.getDuration());

        DateTime dateTime = new DateTime(2011, 4, 8, 19, 03, 45, 69);
        assertEquals(dateTime.toDate(), build.getStartTime());

        assertEquals(State.SUCCESS, build.getState());
        TestResult testResult = build.getUnitTestResult();
        assertEquals(0, testResult.getFailCount());
        assertEquals(331, testResult.getPassCount());
    }

    @Test
    public void should_verify_not_building_project() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        boolean building = bambooPlugin.isBuilding(projectId);
        assertFalse(building);
    }

    @Test
    public void should_verify_success_state() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        State state = bambooPlugin.getState(projectId);
        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_verify_failure_state() throws ProjectNotFoundException {
        ProjectId projectId = struts2ProjectId();
        State state = bambooPlugin.getState(projectId);
        assertEquals(State.FAILURE, state);
    }

    @Ignore
    @Test
    public void should_populate_project() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        Project project = bambooPlugin.findProject(projectId);
        bambooPlugin.populate(project);
        // I don't know where the description is in bamboo rest api!
        // assertEquals("this is the struts project", project.getDescription());
        assertEquals("struts", project.getName());
    }

    @Test
    public void should_get_estimated_finish_time() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        Date estimatedFinishTime = bambooPlugin.getEstimatedFinishTime(projectId);
        assertNotNull(estimatedFinishTime);
    }

    private ProjectId strutsProjectId() {
        ProjectId projectId = new ProjectId();
        projectId.addId(BAMBOO_ID, "STRUTS-STRUTS");
        return projectId;
    }

    private ProjectId struts2ProjectId() {
        ProjectId projectId = new ProjectId();
        projectId.addId(BAMBOO_ID, "STRUTS2INSTABLE-STRUTS2INSTABLE");
        return projectId;
    }
}
