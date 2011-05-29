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

import java.util.Map.Entry;
import java.util.Set;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.BeforeClass;
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
		assertEquals("83.9%", measure.getFormattedValue());
		assertEquals(83.9, measure.getValue(), 0);
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

		assertEquals(40.9, unitTestsAnalysis.getCoverage(), 0);
		assertEquals(8, unitTestsAnalysis.getFailCount());
		assertEquals(0, unitTestsAnalysis.getSkipCount());
		assertEquals(1824, unitTestsAnalysis.getPassCount());
		assertEquals(1832, unitTestsAnalysis.getTotalCount());
	}

	@Test
	public void should_count_exact_it_and_ut() {
		ProjectId projectId = new ProjectId();
		projectId.setArtifactId("fr.xebia.librestry:librestry");

		SonarConnectionPlugin sonarPlugin = new SonarConnectionPlugin();
		sonarPlugin.connect("http://fluxx.fr.cr:9000");
		TestResult unitTestsAnalysis = sonarPlugin.analyzeUnitTests(projectId);
		assertEquals(18.4, unitTestsAnalysis.getCoverage(), 0);
		assertEquals(1, unitTestsAnalysis.getFailCount());
		assertEquals(5, unitTestsAnalysis.getSkipCount());
		assertEquals(3, unitTestsAnalysis.getPassCount());
		assertEquals(9, unitTestsAnalysis.getTotalCount());

		TestResult integrationTestsAnalysis = sonarPlugin.analyzeIntegrationTests(projectId);
		assertEquals(47.4, integrationTestsAnalysis.getCoverage(), 0);
		// Sonar/Jacoco don't count IT tests
		assertEquals(0, integrationTestsAnalysis.getFailCount());
		assertEquals(0, integrationTestsAnalysis.getSkipCount());
		assertEquals(0, integrationTestsAnalysis.getPassCount());
		assertEquals(0, integrationTestsAnalysis.getTotalCount());
	}

}
