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

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.tck.TestCapabilityTCK;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import org.junit.Before;
import org.junit.Test;

public class BambooTestCapabilityIT implements TestCapabilityTCK {

    TestCapability bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
        bamboo.connect(Urls.LOCAL_BAMBOO, null, null);
    }

    @Override
    @Test
    public void should_analyze_unit_tests() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("STR-STRUTSKEY");
        TestResult result = bamboo.analyzeUnitTests(projectId);
        assertEquals(0, result.getFailCount());
        assertEquals(331, result.getPassCount());
        assertEquals(0, result.getSkipCount());
        assertEquals(331, result.getTotalCount());
    }

    @Override
    @Test
    public void should_analyze_integration_tests() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("STR-STRUTSKEY");
        TestResult result = bamboo.analyzeIntegrationTests(projectId);
        assertEquals(0, result.getFailCount());
        assertEquals(0, result.getPassCount());
        assertEquals(0, result.getSkipCount());
        assertEquals(0, result.getTotalCount());
    }

}
