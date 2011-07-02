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
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarResourceNotFoundException;
import net.awired.visuwall.plugin.sonar.resource.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;
import com.google.common.io.ByteStreams;

public class SonarConnection implements MetricCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(SonarConnection.class);

    private final UUID id = UUID.randomUUID();

    @VisibleForTesting
    SonarFinder sonarFinder;

    @VisibleForTesting
    MetricFinder metricFinder;

    @VisibleForTesting
    SonarClient sonarClient;

    private Map<String, QualityMetric> metricsMap;
    private String[] metricKeys = new String[] {};

    private boolean connected;

    public SonarConnection() {
    }

    public void connect(String url) {
        connect(url, null, null);
    }

    @Override
    public void connect(String url, String login, String password) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        if (metricFinder == null) {
            metricFinder = new MetricFinder(url);
        }
        if (sonarFinder == null) {
            sonarFinder = new SonarFinder(url, login, password);
        }
        if (sonarClient == null) {
            sonarClient = new SonarClient(url);
        }
        connected = true;
    }

    public boolean isSonarInstance(URL url) {
        checkConnected();
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
        List<String> projectNames = new ArrayList<String>();
        try {
            List<Project> names = sonarClient.findProjects().getProjects();
            for (Project project : names) {
                projectNames.add(project.getName());
            }
        } catch (ResourceNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return projectNames;
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
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarFinder.findResource(artifactId);
            return resource.getName(true);
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't get description of software project id: " + softwareProjectId,
                    e);
        }
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(projectKey, "projectKey is mandatory");
        try {
            String mavenId = projectKey.getMavenId();
            Resource resource;
            resource = sonarFinder.findResource(mavenId);
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(resource.getKey());
            return softwareProjectId;
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't identify project key: " + projectKey, e);
        }
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        try {
            List<Project> names = sonarClient.findProjects().getProjects();
            for (Project project : names) {
                String key = project.getKey();
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
                softwareProjectIds.add(softwareProjectId);
            }
        } catch (ResourceNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return softwareProjectIds;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        try {
            List<Project> projects = sonarClient.findProjects().getProjects();
            for (Project project : projects) {
                String name = project.getName();
                if (names.contains(name)) {
                    String key = project.getKey();
                    SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
                    softwareProjectIds.add(softwareProjectId);
                }
            }
        } catch (ResourceNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return softwareProjectIds;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        checkConnected();
        checkSoftwareProjectId(projectId);
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
        checkSoftwareProjectId(projectId);
        TestResult integrationTestResult = new TestResult();
        try {
            String artifactId = projectId.getProjectId();
            if (Strings.isNullOrEmpty(artifactId)) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("can't analyze project " + projectId + " without artifactId. Is it a maven project ?");
                }
            } else {
                Double itCoverage = sonarFinder.findMeasure(artifactId, "it_coverage").getValue();
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
        checkSoftwareProjectId(projectId);
        if (metricsMap == null) {
            initializeMetrics();
        }
        if (metrics.length == 0) {
            metrics = metricKeys;
        }
        QualityResult qualityResult = new QualityResult();
        String artifactId = projectId.getProjectId();
        if (!Strings.isNullOrEmpty(artifactId)) {
            for (String key : metrics) {
                addQualityMeasure(qualityResult, artifactId, key);
            }
        }
        return qualityResult;
    }

    private void addQualityMeasure(QualityResult qualityResult, String artifactId, String key) {
        try {
            Measure measure = sonarFinder.findMeasure(artifactId, key);
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

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String artifactId = softwareProjectId.getProjectId();
        try {
            Resource resource = sonarFinder.findResource(artifactId);
            return resource.getKey();
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't get name of software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarFinder.findResource(artifactId);
            return resource.getName();
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't get name of software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return false;
    }

    private TestResult createUnitTestAnalysis(String artifactId) {
        TestResult unitTestResult = new TestResult();
        try {
            Double coverage = sonarFinder.findMeasure(artifactId, "coverage").getValue();
            Double failures = sonarFinder.findMeasure(artifactId, "test_failures").getValue();
            Double errors = sonarFinder.findMeasure(artifactId, "test_errors").getValue();
            Double passTests = sonarFinder.findMeasure(artifactId, "tests").getValue();

            int skipCount = sonarFinder.findMeasure(artifactId, "skipped_tests").getIntValue();
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

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

}
