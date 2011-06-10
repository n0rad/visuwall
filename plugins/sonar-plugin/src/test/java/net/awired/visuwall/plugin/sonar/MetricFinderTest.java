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

package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Map;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.junit.Test;

public class MetricFinderTest {

	@Test
	public void should_create_metrics() throws SonarMetricsNotFoundException {
		final QualityMetric qualityMetric = new QualityMetric();
		qualityMetric.setKey("coverage_key");

		MetricFinder metricsLoader = new MetricFinder("http://sonar") {
			@Override
			SonarMetrics fetchMetrics() {
				SonarMetrics sonarMetrics = new SonarMetrics();

				sonarMetrics.metric = new ArrayList<QualityMetric>();
				sonarMetrics.metric.add(qualityMetric);

				return sonarMetrics;
			}
		};

		Map<String, QualityMetric> metrics = metricsLoader.findMetrics();

		QualityMetric qm = metrics.get("coverage_key");
		assertEquals(qualityMetric, qm);
	}
}
