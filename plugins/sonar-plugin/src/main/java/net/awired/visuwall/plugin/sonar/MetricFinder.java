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

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class MetricFinder {

	private static final Logger LOG = LoggerFactory.getLogger(MetricFinder.class);

	private String sonarUrl;

	public MetricFinder(String sonarUrl) {
		this.sonarUrl = sonarUrl;
	}

	public Map<String, QualityMetric> findMetrics() throws SonarMetricsNotFoundException {
		SonarMetrics sonarMetrics = fetchMetrics();
		Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();
		for (QualityMetric metric : sonarMetrics.metric) {
			qualityMetrics.put(metric.getKey(), metric);
		}
		return qualityMetrics;
	}

	@VisibleForTesting
	SonarMetrics fetchMetrics() throws SonarMetricsNotFoundException {
		String metricUrl = sonarUrl + "/api/metrics?format=xml";
		try {
			InputStream xmlStream = new URL(metricUrl).openStream();
			Unmarshaller unmarshaller = JAXBContext.newInstance(SonarMetrics.class).createUnmarshaller();
			SonarMetrics sonarMetrics = SonarMetrics.class.cast(unmarshaller.unmarshal(xmlStream));
			return sonarMetrics;
		} catch (Exception e) {
			String msg = "metric can't be found at url: " + metricUrl;
			LOG.error(msg, e);
			throw new SonarMetricsNotFoundException(msg, e);
		}
	}

}
