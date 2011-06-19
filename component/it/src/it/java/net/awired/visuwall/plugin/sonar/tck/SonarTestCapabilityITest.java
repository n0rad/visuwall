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

package net.awired.visuwall.plugin.sonar.tck;

import static net.awired.visuwall.IntegrationTestData.SONAR_URL;
import static net.awired.visuwall.IntegrationTestData.STRUTS_2_ARTIFACT_ID;
import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.tck.TestCapabilityTCK;
import net.awired.visuwall.plugin.sonar.SonarConnection;

import org.junit.BeforeClass;
import org.junit.Test;

public class SonarTestCapabilityITest implements TestCapabilityTCK {

	private static TestCapability sonar = new SonarConnection();

    @BeforeClass
    public static void init() throws ConnectionException {
		((Connection) sonar).connect(SONAR_URL, null, null);
    }

    @Test
    public void should_analyze_unit_tests() {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId(STRUTS_2_ARTIFACT_ID);

        TestResult unitTestsAnalysis = sonar.analyzeUnitTests(projectId);

        assertEquals(40.9, unitTestsAnalysis.getCoverage(), 0);
        assertEquals(8, unitTestsAnalysis.getFailCount());
        assertEquals(0, unitTestsAnalysis.getSkipCount());
        assertEquals(1824, unitTestsAnalysis.getPassCount());
        assertEquals(1832, unitTestsAnalysis.getTotalCount());
    }

    @Test
    public void should_analyze_integration_tests() throws ConnectionException {
		TestCapability sonar = new SonarConnection();
		((Connection) sonar).connect("http://fluxx.fr.cr:9000", null, null);

		ProjectId projectId = new ProjectId();
        projectId.setArtifactId("fr.xebia.librestry:librestry");

		TestResult integrationTestsAnalysis = sonar.analyzeIntegrationTests(projectId);
        assertEquals(47.4, integrationTestsAnalysis.getCoverage(), 0);
        // Sonar/Jacoco don't count IT tests
        assertEquals(0, integrationTestsAnalysis.getFailCount());
        assertEquals(0, integrationTestsAnalysis.getSkipCount());
        assertEquals(0, integrationTestsAnalysis.getPassCount());
        assertEquals(0, integrationTestsAnalysis.getTotalCount());
    }

}
