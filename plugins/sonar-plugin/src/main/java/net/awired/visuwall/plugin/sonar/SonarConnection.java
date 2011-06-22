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

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.services.Measure;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

public class SonarConnection implements MetricCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(SonarConnection.class);

    private final UUID id = UUID.randomUUID();

    @VisibleForTesting
    MeasureFinder measureFinder;

    @VisibleForTesting
    MetricFinder metricFinder;

    private Map<String, QualityMetric> metricsMap;
    private String[] metricKeys = new String[] {};

    private boolean connected;

    public SonarConnection() {
    }

    public void connect(String url) throws ConnectionException {
        connect(url, null, null);
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        if (metricFinder == null) {
            metricFinder = new MetricFinder(url);
        }
        if (measureFinder == null) {
            measureFinder = new MeasureFinder(url, login, password);
        }
        connected = true;
    }

    public boolean isSonarInstance(URL url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            url = new URL(url.toString() + "/api/properties");
            byte[] content = ByteStreams.toByteArray(url.openStream());
            String xml = new String(content);
            return xml.contains("sonar.core.version");
        } catch (IOException e) {
            return false;
        }
    }

    @Override
    public Map<String, List<QualityMetric>> getMetricsByCategory() {
        checkConnected();
        if (metricsMap == null) {
            initializeMetrics();
        }
        Map<String, List<QualityMetric>> metricsByDomain = new HashMap<String, List<QualityMetric>>();
        for (QualityMetric metricValue : metricsMap.values()) {
            String domain = metricValue.getDomain();
            List<QualityMetric> domainMetrics = metricsByDomain.get(domain);
            if (domainMetrics == null) {
                domainMetrics = new ArrayList<QualityMetric>();
                metricsByDomain.put(domain, domainMetrics);
            }
            domainMetrics.add(metricValue);
        }
        return metricsByDomain;
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        return new ArrayList<String>();
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof SonarConnection) {
            SonarConnection s = (SonarConnection) o;
            return id.equals(s.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id);
    }

    @Override
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        return new ArrayList<SoftwareProjectId>();
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        return new ArrayList<SoftwareProjectId>();
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        checkConnected();
        checkProjectId(projectId);
        TestResult unitTestResult = new TestResult();
        String artifactId = projectId.getProjectId();
        if (Strings.isNullOrEmpty(artifactId)) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("can't analyze project " + projectId + " without artifactId. Is it a maven project ?");
            }
        } else {
            unitTestResult = createUnitTestAnalysis(artifactId);
        }
        return unitTestResult;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId projectId) {
        checkConnected();
        checkProjectId(projectId);
        TestResult integrationTestResult = new TestResult();
        try {
            String artifactId = projectId.getProjectId();
            if (Strings.isNullOrEmpty(artifactId)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("can't analyze project " + projectId + " without artifactId. Is it a maven project ?");
                }
            } else {
                Double itCoverage = measureFinder.findMeasure(artifactId, "it_coverage").getValue();
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

    @Override
    public QualityResult analyzeQuality(SoftwareProjectId projectId, String... metrics) {
        checkConnected();
        checkProjectId(projectId);
        if (metricsMap == null) {
            initializeMetrics();
        }
        QualityResult qualityResult = new QualityResult();
        String artifactId = projectId.getProjectId();
        if (!Strings.isNullOrEmpty(artifactId)) {
            if (metrics.length == 0) {
                metrics = metricKeys;
            }
            for (String key : metrics) {
                try {
                    Measure measure = measureFinder.findMeasure(artifactId, key);
                    if (measure.getValue() != null) {
                        QualityMeasure qualityMeasure = QualityMeasures.asQualityMeasure(measure, key);
                        qualityMeasure.setName(metricsMap.get(key).getName());
                        qualityResult.add(key, qualityMeasure);
                    }
                } catch (SonarMeasureNotFoundException e) {
                    if (LOG.isDebugEnabled()) {
                        LOG.debug(e.getMessage());
                    }
                }
            }
        }
        return qualityResult;
    }

    private TestResult createUnitTestAnalysis(String artifactId) {
        TestResult unitTestResult = new TestResult();
        try {
            Double coverage = measureFinder.findMeasure(artifactId, "coverage").getValue();
            Double failures = measureFinder.findMeasure(artifactId, "test_failures").getValue();
            Double errors = measureFinder.findMeasure(artifactId, "test_errors").getValue();
            Double passTests = measureFinder.findMeasure(artifactId, "tests").getValue();

            int skipCount = measureFinder.findMeasure(artifactId, "skipped_tests").getIntValue();
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
        return unitTestResult;
    }

    private void initializeMetrics() {
        try {
        metricsMap = metricFinder.findMetrics();
        Set<String> metricKeysSet = metricsMap.keySet();
        int countMetricKeys = metricKeysSet.size();
        metricKeys = metricKeysSet.toArray(new String[countMetricKeys]);
        } catch (SonarMetricsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't initialize metrics", e);
            }
        }
    }

    private void checkProjectId(SoftwareProjectId projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

}
