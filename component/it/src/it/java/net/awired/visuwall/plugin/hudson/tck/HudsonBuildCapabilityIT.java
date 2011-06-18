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

import static net.awired.visuwall.plugin.hudson.HudsonConnection.HUDSON_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.Date;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BuildCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;

import org.junit.Before;
import org.junit.Test;

public class HudsonBuildCapabilityIT implements BuildCapabilityTCK {

	BuildCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
		((Connection) hudson).connect(IntegrationTestData.HUDSON_URL, null, null);
    }

	@Override
    @Test
	public void should_find_build_by_build_number() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        Build build = hudson.findBuildByBuildNumber(projectId, 1);
        assertNotNull(build);
    }

	@Override
    @Test
	public void should_get_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        int buildNumber = hudson.getLastBuildNumber(projectId);
        assertEquals(1, buildNumber);
    }

	@Override
    @Test
	public void should_get_is_building() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        boolean building = hudson.isBuilding(projectId);
        assertFalse(building);
    }

	@Override
    @Test
	public void should_get_success_build_state() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
		State state = hudson.getLastBuildState(projectId);
        assertEquals(State.SUCCESS, state);
    }

    @Override
    @Test
    public void should_get_estimated_date() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        Date date = hudson.getEstimatedFinishTime(projectId);
        assertNotNull(date);
    }
}
