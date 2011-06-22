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

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.plugin.bamboo.BambooConnection;

import org.junit.Before;
import org.junit.Test;

public class BambooBuildCapabilityIT {

	BuildCapability bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
        bamboo.connect(BAMBOO_URL, null, null);
	}

    @Test
    public void should_find_last_build_number() throws Exception {
        SoftwareProjectId projectId = strutsProjectId();
        int buildNumber = bamboo.getLastBuildNumber(projectId);
        assertEquals(3, buildNumber);
    }

    @Test
    public void should_verify_not_building_project() throws ProjectNotFoundException {
        SoftwareProjectId projectId = strutsProjectId();
        boolean building = bamboo.isBuilding(projectId);
        assertFalse(building);
    }

    @Test
    public void should_verify_success_state() throws ProjectNotFoundException {
        SoftwareProjectId projectId = strutsProjectId();
        State state = bamboo.getLastBuildState(projectId);
        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_verify_failure_state() throws ProjectNotFoundException {
        SoftwareProjectId projectId = struts2ProjectId();
        State state = bamboo.getLastBuildState(projectId);
        assertEquals(State.FAILURE, state);
    }

    @Test
    public void should_get_estimated_finish_time() throws ProjectNotFoundException {
        SoftwareProjectId projectId = strutsProjectId();
        Date estimatedFinishTime = bamboo.getEstimatedFinishTime(projectId);
        assertNotNull(estimatedFinishTime);
    }
    
    private SoftwareProjectId strutsProjectId() {
        return new SoftwareProjectId("STRUTS-STRUTS");
    }

    private SoftwareProjectId struts2ProjectId() {
        return new SoftwareProjectId("STRUTS2INSTABLE-STRUTS2INSTABLE");
    }
}
