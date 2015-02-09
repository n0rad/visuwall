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

package fr.norad.visuwall.plugin.continuum;

import static java.util.Arrays.asList;

import java.util.List;

import fr.norad.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class ContinuumConnectionIT {

    @Test
    public void test() throws Exception {

        ContinuumConnection connection = new ContinuumConnection();
        connection.connect("http://vmbuild.apache.org/continuum", "", "");

        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(asList("Apache Commons"));
        for (SoftwareProjectId softwareProjectId : softwareProjectIds) {
            System.out.println("----");
            String buildId = connection.getLastBuildId(softwareProjectId);
            connection.getName(softwareProjectId);
            connection.getMavenId(softwareProjectId);
            connection.getBuildCommiters(softwareProjectId, buildId);
            connection.getBuildTime(softwareProjectId, buildId);
            connection.getDescription(softwareProjectId);
            connection.getBuildIds(softwareProjectId);
            connection.getBuildState(softwareProjectId, buildId);
            connection.getEstimatedFinishTime(softwareProjectId, buildId);
            connection.findViews();
        }
    }
}
