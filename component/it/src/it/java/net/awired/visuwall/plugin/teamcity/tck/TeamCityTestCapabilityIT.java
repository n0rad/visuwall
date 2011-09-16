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
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.tck.TestCapabilityTCK;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;
import org.junit.Before;
import org.junit.Test;

public class TeamCityTestCapabilityIT implements TestCapabilityTCK {

    TestCapability teamcity = new TeamCityConnection();

    @Before
    public void init() throws ConnectionException {
        teamcity.connect(Urls.LOCAL_TEAMCITY, null, null);
    }

    @Override
    @Test
    public void should_analyze_unit_tests() throws Exception {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("IT coverage");
        SoftwareProjectId projectId = teamcity.identify(projectKey);
        TestResult unitTests = teamcity.analyzeUnitTests(projectId);
        assertEquals(3, unitTests.getPassCount());
        assertEquals(1, unitTests.getFailCount());
        assertEquals(5, unitTests.getSkipCount());
        assertEquals(9, unitTests.getTotalCount());
    }

    @Override
    @Test
    public void should_analyze_integration_tests() throws Exception {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("IT coverage");
        SoftwareProjectId projectId = teamcity.identify(projectKey);
        TestResult integrationTests = teamcity.analyzeIntegrationTests(projectId);
        assertEquals(0, integrationTests.getPassCount());
        assertEquals(0, integrationTests.getFailCount());
        assertEquals(0, integrationTests.getSkipCount());
        assertEquals(0, integrationTests.getTotalCount());
    }

}
