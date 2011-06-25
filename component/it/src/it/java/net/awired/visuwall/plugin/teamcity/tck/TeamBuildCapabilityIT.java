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

package net.awired.visuwall.plugin.teamcity.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TeamBuildCapabilityIT implements BuildCapabilityTCK {

    BuildCapability teamcity = new TeamCityConnection();

    @Before
    public void init() throws ConnectionException {
        teamcity.connect(IntegrationTestData.TEAMCITY_URL, "guest", "");
    }

    @Override
    @Test
    public void should_get_build_numbers() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        List<Integer> buildNumbers = teamcity.getBuildNumbers(projectId);

        assertEquals(Arrays.asList(1, 2, 3, 4, 5, 6), buildNumbers);
    }

    @Override
    @Test
    @Ignore
    public void should_get_estimated_date() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        Date estimatedFinishTime = teamcity.getEstimatedFinishTime(projectId, 1);

        assertNotNull(estimatedFinishTime);
    }

    @Override
    @Test
    public void should_get_last_build_number() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        int lastBuildNumber = teamcity.getLastBuildNumber(projectId);

        assertEquals(6, lastBuildNumber);
    }

    @Override
    @Test
    public void should_get_build_state() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        State state = teamcity.getBuildState(projectId, 6);

        assertEquals(State.SUCCESS, state);
    }

    @Override
    @Test
    public void should_get_is_building() throws Exception {
        SoftwareProjectId projectId = amazonProjectSoftwareId();
        boolean isBuilding = teamcity.isBuilding(projectId, 1);

        assertFalse(isBuilding);
    }

    @Test
    @Override
    public void should_get_build_time() throws Exception {
        SoftwareProjectId softwareProjectId = amazonProjectSoftwareId();
        int buildNumber = teamcity.getLastBuildNumber(softwareProjectId);
        BuildTime buildTime = teamcity.getBuildTime(softwareProjectId, buildNumber);
        assertNotNull(buildTime);
    }

    private SoftwareProjectId amazonProjectSoftwareId() {
        return new SoftwareProjectId("project54");
    }

}
