/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.plugin.sonar;

import static net.awired.visuwall.IntegrationTestData.SONAR_URL;
import static net.awired.visuwall.IntegrationTestData.STRUTS_2_ARTIFACT_ID;
import static net.awired.visuwall.IntegrationTestData.STRUTS_ARTIFACT_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map.Entry;
import java.util.Set;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SonarConnectionPluginIT {

	private static SonarConnectionPlugin sonarPlugin;

	@BeforeClass
	public static void init() {
		sonarPlugin = new SonarConnectionPlugin();
		sonarPlugin.connect(SONAR_URL);
	}

	@Test
	public void should_populate_quality() {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId(STRUTS_ARTIFACT_ID);
		QualityResult quality = sonarPlugin.analyzeQuality(projectId, "violations_density");
		QualityMeasure measure = quality.getMeasure("violations_density");
		assertEquals("Rules compliance", measure.getName());
		assertEquals("77.2%", measure.getFormattedValue());
		assertEquals(77.2, measure.getValue(), 0);
	}

	@Test
	public void should_have_a_lot_of_quality_metrics() {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId(STRUTS_ARTIFACT_ID);
		QualityResult quality = sonarPlugin.analyzeQuality(projectId);
		Set<Entry<String, QualityMeasure>> measures = quality.getMeasures();
		for (Entry<String, QualityMeasure> measure : measures) {
			assertNotNull(measure.getValue().getValue());
		}
	}

	@Test
	public void should_not_fail_if_measure_does_not_exist() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId(STRUTS_ARTIFACT_ID);
		sonarPlugin.analyzeQuality(projectId, "inexistant_measure");
	}

	@Test
	public void should_analyze_unit_tests() {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId(STRUTS_2_ARTIFACT_ID);

		TestResult unitTestsAnalysis = sonarPlugin.analyzeUnitTests(projectId);

		assertEquals(39.4, unitTestsAnalysis.getCoverage(), 0);
		assertEquals(8, unitTestsAnalysis.getFailCount());
		assertEquals(0, unitTestsAnalysis.getSkipCount());
		assertEquals(1821, unitTestsAnalysis.getPassCount());
		assertEquals(1829, unitTestsAnalysis.getTotalCount());
	}

	@Ignore("we have to create a dummy project and install jacoco on awired CI")
	@Test
	public void should_analyze_integration_tests() {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId("com.orangevallee.on.server.synthesis:synthesis");
		SonarConnectionPlugin sonarConnectionPlugin = new SonarConnectionPlugin();
		sonarConnectionPlugin.connect("http://10.2.40.60/lifeisbetteron/sonar");

		TestResult integrationTestsAnalysis = sonarConnectionPlugin.analyzeIntegrationTests(projectId);

		assertEquals(5.0, integrationTestsAnalysis.getCoverage(), 0);
	}

	@Test
	public void should_recognize_sonar_instance_with_valid_url() throws MalformedURLException {
		SonarConnectionPlugin sonarConnectionPlugin = new SonarConnectionPlugin();
		boolean isSonarInstance = sonarConnectionPlugin.isSonarInstance(new URL(SONAR_URL));
		assertTrue(isSonarInstance);
	}

}
