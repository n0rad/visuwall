/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.plugin.teamcity;

import static org.junit.Assert.assertNotNull;

import java.util.Date;

import fr.norad.visuwall.api.domain.BuildState;
import fr.norad.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class TeamCityConnectionIT {

    @Test
    public void test() throws Exception {
        TeamCityConnection connection = new TeamCityConnection();
        connection.connect("http://localhost:8111", "guest", "");
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("bt15");
        String lastBuildId = connection.getLastBuildId(softwareProjectId);
        System.out.println("Last build id:" + lastBuildId);
        boolean building = connection.isBuilding(softwareProjectId, lastBuildId);
        System.out.println("building:" + building);
        BuildState buildState = connection.getBuildState(softwareProjectId, lastBuildId);
        System.out.println("State:" + buildState);
        Date estimatedFinishTime = connection.getEstimatedFinishTime(softwareProjectId, lastBuildId);
        assertNotNull(estimatedFinishTime);
        System.out.println(estimatedFinishTime);
    }

}
