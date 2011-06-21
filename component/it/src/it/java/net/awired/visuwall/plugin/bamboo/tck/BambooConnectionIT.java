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

package net.awired.visuwall.plugin.bamboo.tck;

import static net.awired.visuwall.IntegrationTestData.BAMBOO_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.plugin.bamboo.BambooConnection;

import org.joda.time.DateTime;
import org.junit.Before;
import org.junit.Test;

public class BambooConnectionIT {

	BambooConnection bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
		((Connection) bamboo).connect(BAMBOO_URL, null, null);
	}

    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = bamboo.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_project() throws ProjectNotFoundException {
        ProjectId projectId = struts2ProjectId();
        Project project = bamboo.findProject(projectId);
        assertNotNull(project);
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        ProjectId projectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        assertEquals(3, buildNumber);
    }

    @Test
    public void should_find_build_by_name_and_build_number() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        Build build = bamboo.findBuildByBuildNumber(projectId, 3);
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
        boolean building = bamboo.isBuilding(projectId);
        assertFalse(building);
    }

    @Test
    public void should_verify_success_state() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        State state = bamboo.getLastBuildState(projectId);
        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_verify_failure_state() throws ProjectNotFoundException {
        ProjectId projectId = struts2ProjectId();
        State state = bamboo.getLastBuildState(projectId);
        assertEquals(State.FAILURE, state);
    }

    @Test
    public void should_get_estimated_finish_time() throws ProjectNotFoundException {
        ProjectId projectId = strutsProjectId();
        Date estimatedFinishTime = bamboo.getEstimatedFinishTime(projectId);
        assertNotNull(estimatedFinishTime);
    }

    private ProjectId strutsProjectId() {
        ProjectId projectId = new ProjectId();
		projectId.addId(BambooConnection.BAMBOO_ID, "STRUTS-STRUTS");
        return projectId;
    }

    private ProjectId struts2ProjectId() {
        ProjectId projectId = new ProjectId();
		projectId.addId(BambooConnection.BAMBOO_ID, "STRUTS2INSTABLE-STRUTS2INSTABLE");
        return projectId;
    }
}
