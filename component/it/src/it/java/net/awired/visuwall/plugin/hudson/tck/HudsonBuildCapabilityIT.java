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

package net.awired.visuwall.plugin.hudson.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.Date;
import java.util.List;
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
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import org.junit.Before;
import org.junit.Test;

public class HudsonBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
        hudson.connect(IntegrationTestData.HUDSON_URL, null, null);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("struts");
        int buildNumber = hudson.getLastBuildNumber(projectId);
        assertEquals(1, buildNumber);
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("struts");
        int buildNumber = hudson.getLastBuildNumber(projectId);
        boolean building = hudson.isBuilding(projectId, buildNumber);
        assertFalse(building);
    }

    @Override
    @Test
    public void should_get_build_state() throws Exception {
        assertEquals(State.SUCCESS, getLastBuildState("struts"));
        assertEquals(State.UNSTABLE, getLastBuildState("fluxx"));
        assertEquals(State.FAILURE, getLastBuildState("dev-radar"));
    }

    private State getLastBuildState(String name) throws ProjectNotFoundException, BuildNumberNotFoundException,
            BuildNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId(name);
        int buildNumber = hudson.getLastBuildNumber(projectId);
        State state = hudson.getBuildState(projectId, buildNumber);
        return state;
    }

    @Override
    @Test
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        int buildNumber = hudson.getLastBuildNumber(softwareProjectId);
        Date date = hudson.getEstimatedFinishTime(softwareProjectId, buildNumber);
        assertNotNull(date);
    }

    @Override
    @Test
    public void should_get_build_numbers() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("dev-radar-sonar");
        List<Integer> buildNumbers = hudson.getBuildNumbers(softwareProjectId);
        assertEquals(25, buildNumbers.get(0).intValue());
        assertEquals(68, buildNumbers.get(1).intValue());
        assertEquals(69, buildNumbers.get(2).intValue());
    }

    @Override
    @Test
    public void should_get_build_time() throws Exception {
        SoftwareProjectId softwareProjectId = struts();
        int buildNumber = hudson.getLastBuildNumber(softwareProjectId);
        BuildTime buildTime = hudson.getBuildTime(softwareProjectId, buildNumber);
        assertNotNull(buildTime);
    }

    private SoftwareProjectId struts() {
        return new SoftwareProjectId("struts");
    }
}
