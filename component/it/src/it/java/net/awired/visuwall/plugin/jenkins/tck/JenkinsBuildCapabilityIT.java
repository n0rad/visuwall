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

package net.awired.visuwall.plugin.jenkins.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.Date;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class JenkinsBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability jenkins = new JenkinsConnection();

    @Before
    public void init() throws ConnectionException {
        jenkins.connect(IntegrationTestData.JENKINS_URL, null, null);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = struts();
        int number = jenkins.getLastBuildNumber(projectId);
        assertEquals(4, number);
    }

    @Ignore("refaire le calcul du UNSTABLE, peut etre le sortir et mettre dans core")
    @Override
    @Test
    public void should_get_build_state() throws Exception {
        assertEquals(State.SUCCESS, getLastBuildState("struts"));
        assertEquals(State.FAILURE, getLastBuildState("errorproject"));
        assertEquals(State.UNSTABLE, getLastBuildState("itcoverage-project"));
    }

    private State getLastBuildState(String jobName) throws ProjectNotFoundException, BuildNumberNotFoundException,
            BuildNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId(jobName);
        int buildNumber = jenkins.getLastBuildNumber(projectId);
        State state = jenkins.getBuildState(projectId, buildNumber);
        return state;
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId projectId = struts();
        int buildNumber = jenkins.getLastBuildNumber(projectId);
        boolean isBuilding = jenkins.isBuilding(projectId, buildNumber);
        assertFalse(isBuilding);
    }

    @Override
    @Test
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId projectId = struts();
        int buildNumber = jenkins.getLastBuildNumber(projectId);
        Date date = jenkins.getEstimatedFinishTime(projectId, buildNumber);
        assertNotNull(date);
    }

    private SoftwareProjectId struts() {
        return new SoftwareProjectId("struts");
    }

    @Override
    @Test
    @Ignore
    public void should_get_build_numbers() throws Exception {

    }

    @Override
    @Test
    public void should_get_build_time() throws Exception {
        SoftwareProjectId projectId = struts();
        int buildNumber = jenkins.getLastBuildNumber(projectId);
        BuildTime buildTime = jenkins.getBuildTime(projectId, buildNumber);
        assertNotNull(buildTime);
    }
}
