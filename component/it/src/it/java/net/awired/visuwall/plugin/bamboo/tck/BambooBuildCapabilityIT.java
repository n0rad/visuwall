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
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import org.junit.Before;
import org.junit.Test;

public class BambooBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
        bamboo.connect(BAMBOO_URL, null, null);
    }

    private SoftwareProjectId strutsProjectId() {
        return new SoftwareProjectId("STRUTS-STRUTS");
    }

    private SoftwareProjectId struts2ProjectId() {
        return new SoftwareProjectId("STRUTS2INSTABLE-STRUTS2INSTABLE");
    }

    @Override
    @Test
    public void should_get_build_numbers() throws Exception {
        List<Integer> buildNumbers = bamboo.getBuildNumbers(strutsProjectId());
        assertEquals(2, buildNumbers.get(0).intValue());
        assertEquals(3, buildNumbers.get(1).intValue());
    }

    @Override
    @Test
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId projectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        Date estimatedFinishTime = bamboo.getEstimatedFinishTime(projectId, buildNumber);
        assertNotNull(estimatedFinishTime);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        assertEquals(3, buildNumber);
    }

    @Override
    @Test
    public void should_get_build_state() throws Exception {
        SoftwareProjectId projectId = struts2ProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        State state = bamboo.getBuildState(projectId, buildNumber);
        assertEquals(State.FAILURE, state);

        projectId = strutsProjectId();
        buildNumber = bamboo.getLastBuildNumber(projectId);
        state = bamboo.getBuildState(projectId, buildNumber);
        assertEquals(State.SUCCESS, state);
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId projectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        boolean building = bamboo.isBuilding(projectId, buildNumber);
        assertFalse(building);
    }

    @Override
    @Test
    public void should_get_build_time() throws Exception {
        SoftwareProjectId softwareProjectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(softwareProjectId);
        BuildTime buildTime = bamboo.getBuildTime(softwareProjectId, buildNumber);
        assertNotNull(buildTime);
    }
}
