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

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.tck.TestCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;
import org.junit.Before;

public class JenkinsTestCapabilityIT implements TestCapabilityTCK {

    TestCapability jenkins = new JenkinsConnection();

    @Before
    public void init() throws ConnectionException {
        jenkins.connect(IntegrationTestData.HUDSON_URL, null, null);
    }

    @Override
    public void should_analyze_unit_tests() throws Exception {
        //        SoftwareProjectId projectId = new SoftwareProjectId("visuwall");
        //        TestResult integrationTests = jenkins.analyzeIntegrationTests(projectId);
        //        Assert.assertEquals(105, integrationTests.getPassCount());
    }

    @Override
    public void should_analyze_integration_tests() throws Exception {
        // TODO Auto-generated method stub

    }

}
