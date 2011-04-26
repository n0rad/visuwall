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

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

public class MeasureFinderTest {

	MetricFinder metricsListBuilder = Mockito.mock(MetricFinder.class);

	@Before
	public void init() throws SonarMetricsNotFoundException {
		QualityMetric qualityMetric = new QualityMetric();
		qualityMetric.setName("Coverage");
		qualityMetric.setKey("coverage");

		Map<String, QualityMetric> metricList = new HashMap<String, QualityMetric>();
		metricList.put("coverage", qualityMetric);

		when(metricsListBuilder.findMetrics()).thenReturn(metricList);
	}

	@Test
	public void testFindQualityMeasure() throws SonarMetricNotFoundException {
		Measure coverageMeasure = new Measure();
		coverageMeasure.setFormattedValue("5%");
		coverageMeasure.setValue(5D);
		coverageMeasure.setMetricName("Coverage");
		coverageMeasure.setMetricKey("coverage");

		Resource resource = Mockito.mock(Resource.class);
		when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

		Sonar sonar = Mockito.mock(Sonar.class);
		when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource);

		Map<String, QualityMetric> metrics = new HashMap<String, QualityMetric>();
		QualityMetric qualityMetric = new QualityMetric();
		qualityMetric.setName("Coverage");
		metrics.put("coverage", qualityMetric);

		MeasureFinder measureFinder = new MeasureFinder(sonar);

		QualityMeasure qualityMeasure = measureFinder.findQualityMeasure("projectId", "coverage");

		assertEquals(coverageMeasure.getFormattedValue(), qualityMeasure.getFormattedValue());
		assertEquals(coverageMeasure.getValue(), qualityMeasure.getValue());
		assertEquals(coverageMeasure.getMetricKey(), qualityMeasure.getKey());
	}

	@Test
	public void testFindMeasure() throws SonarMetricNotFoundException {
		Measure coverageMeasure = new Measure();
		coverageMeasure.setFormattedValue("5%");
		coverageMeasure.setValue(5D);
		coverageMeasure.setMetricName("Coverage");

		Resource resource = Mockito.mock(Resource.class);
		when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

		Sonar sonar = Mockito.mock(Sonar.class);
		when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource);

		MeasureFinder measureFinder = new MeasureFinder(sonar);

		ProjectId projectId = new ProjectId();
		projectId.setArtifactId("artifactId");

		Measure measure = measureFinder.findMeasure("artifactId", "coverage");

		assertEquals(coverageMeasure.getFormattedValue(), measure.getFormattedValue());
		assertEquals(coverageMeasure.getValue(), measure.getValue());
		assertEquals(coverageMeasure.getMetricName(), measure.getMetricName());
	}

}
