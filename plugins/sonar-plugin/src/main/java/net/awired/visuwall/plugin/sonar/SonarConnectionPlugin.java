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

import java.io.IOException;
import java.net.URL;
import java.util.Map;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

public final class SonarConnectionPlugin implements QualityConnectionPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(SonarConnectionPlugin.class);

	private MeasureFinder measureFinder;

	private MetricFinder metricListBuilder;

	private Map<String, QualityMetric> metricsMap;
	private String[] metricKeys = new String[] {};

	private boolean connected;

	public SonarConnectionPlugin() {
	}

	public void connect(String url) {
		connect(url, null, null);
	}

	public void connect(String url, String login, String password) {
		if (LOG.isInfoEnabled()) {
			LOG.info("Initialize sonar with url " + url);
		}
		try {
			measureFinder = new MeasureFinder(url, login, password);
			metricListBuilder = new MetricFinder(url);
			metricsMap = metricListBuilder.findMetrics();
			metricKeys = metricsMap.keySet().toArray(new String[] {});
			connected = true;
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
			connected = true;
		} catch (SonarMetricsNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	@Override
	public QualityResult analyzeQuality(ProjectId projectId, String... metrics) {
		Preconditions.checkNotNull(projectId, "projectId");
		Preconditions.checkState(connected, "You must connect your plugin");

		QualityResult qualityResult = new QualityResult();
		String artifactId = projectId.getArtifactId();
		if (!Strings.isNullOrEmpty(artifactId)) {
			if (metrics.length == 0) {
				metrics = metricKeys;
			}
			for (String key : metrics) {
				try {
					QualityMeasure qualityMeasure = measureFinder.findQualityMeasure(artifactId, key);
					qualityMeasure.setName(metricsMap.get(key).getName());
					qualityResult.add(key, qualityMeasure);
				} catch (SonarMeasureNotFoundException e) {
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
		Preconditions.checkState(connected, "You must connect your plugin");

		String artifactId = projectId.getArtifactId();
		if (artifactId == null) {
			return false;
		}

		try {
			measureFinder.findMeasure(artifactId, "comment_blank_lines");
		} catch (SonarMeasureNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	public TestResult analyzeUnitTests(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		TestResult unitTestResult = new TestResult();
		String artifactId = projectId.getArtifactId();
		if (Strings.isNullOrEmpty(artifactId)) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("can't analyze project " + projectId + " without artifactId. Is it a maven project ?");
			}

		} else {
			insertUnitTestAnalysis(artifactId, unitTestResult);
		}
		return unitTestResult;
	}

	private void insertUnitTestAnalysis(String artifactId, TestResult unitTestResult) {
		try {
			Double coverage = measureFinder.findMeasureValue(artifactId, "coverage");
			Double failures = measureFinder.findMeasureValue(artifactId, "test_failures");
			Double errors = measureFinder.findMeasureValue(artifactId, "test_errors");
			Double passTests = measureFinder.findMeasureValue(artifactId, "tests");

			int skipCount = measureFinder.findMeasureValue(artifactId, "skipped_tests").intValue();
			int failCount = failures.intValue() + errors.intValue();
			int passCount = passTests.intValue() - failCount;

			unitTestResult.setCoverage(coverage);
			unitTestResult.setFailCount(failCount);
			unitTestResult.setSkipCount(skipCount);
			unitTestResult.setPassCount(passCount);
		} catch (SonarMeasureNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Unit tests informations are not available for project with artifactId : " + artifactId
				        + ", cause " + e.getMessage());
			}
		}
	}

	@Override
	public TestResult analyzeIntegrationTests(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		TestResult integrationTestResult = new TestResult();

		try {
			String artifactId = projectId.getArtifactId();
			if (Strings.isNullOrEmpty(artifactId)) {
				if (LOG.isDebugEnabled()) {
					LOG.debug("can't analyze project " + projectId + " without artifactId. Is it a maven project ?");
				}
			} else {
				Double itCoverage = measureFinder.findMeasureValue(artifactId, "it_coverage");
				integrationTestResult.setCoverage(itCoverage);
			}
		} catch (SonarMeasureNotFoundException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("Integration tests informations are not available for project " + projectId + ", cause "
				        + e.getMessage());
			}
		}
		return integrationTestResult;
	}

	public boolean isSonarInstance(URL url) {
		Preconditions.checkNotNull(url, "url is mandatory");
		try {
			url = new URL(url.toString() + "/api/properties");
			byte[] content = ByteStreams.toByteArray(url.openStream());
			String xml = new String(content);
			return xml.contains("sonar.core.version");
		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
}
