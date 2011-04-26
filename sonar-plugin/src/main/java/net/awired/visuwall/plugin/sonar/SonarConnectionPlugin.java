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

import java.util.Map;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class SonarConnectionPlugin implements QualityConnectionPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(SonarConnectionPlugin.class);

	private MeasureFinder measureFinder;

	private MetricFinder metricListBuilder;

	private Map<String, QualityMetric> metricsMap;
	private String[] metricKeys = new String[] {};

	public SonarConnectionPlugin(String url) {
		this(url, null, null);
	}

	public SonarConnectionPlugin(String url, String login, String password) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialize sonar with url " + url);
		}
		try {
			measureFinder = new MeasureFinder(url, login, password);
			metricListBuilder = new MetricFinder(url);
			metricsMap = metricListBuilder.findMetrics();
			metricKeys = metricsMap.keySet().toArray(new String[] {});
		} catch (SonarMetricsNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@VisibleForTesting
	SonarConnectionPlugin(MeasureFinder measureFinder, MetricFinder metricListBuilder) {
		try {
			this.measureFinder = measureFinder;
			metricsMap = metricListBuilder.findMetrics();
			metricKeys = metricsMap.keySet().toArray(new String[] {});
		} catch (SonarMetricsNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public QualityResult populateQuality(ProjectId projectId, String... metrics) {
		Preconditions.checkNotNull(projectId, "projectId");

		QualityResult qualityResult = new QualityResult();
		if (projectId.getArtifactId() != null) {
			if (metrics.length == 0) {
				metrics = metricKeys;
			}
			for (String key : metrics) {
				try {
					QualityMeasure qualityMeasure = measureFinder.findQualityMeasure(projectId.getArtifactId(), key);
					qualityMeasure.setName(metricsMap.get(key).getName());
					qualityResult.add(key, qualityMeasure);
				} catch (SonarMetricNotFoundException e) {
					if (LOG.isDebugEnabled()) {
						LOG.debug(e.getMessage());
					}
				}
			}
		}
		return qualityResult;
	}

	@Override
	public boolean contains(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");

		String artifactId = projectId.getArtifactId();
		if (artifactId == null) {
			return false;
		}

		try {
			measureFinder.findMeasure(artifactId, "comment_blank_lines");
		} catch (SonarMetricNotFoundException e) {
			return false;
		}
		return true;
	}

}
