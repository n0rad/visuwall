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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import net.awired.clients.sonar.Sonar;
import net.awired.clients.sonar.domain.SonarQualityMetric;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.exception.SonarProjectNotFoundException;
import net.awired.clients.sonar.exception.SonarProjectsNotFoundException;
import net.awired.clients.sonar.exception.SonarResourceNotFoundException;
import net.awired.clients.sonar.resource.Project;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SonarConnection implements MetricCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(SonarConnection.class);

    private final UUID id = UUID.randomUUID();

    @VisibleForTesting
    Sonar sonarClient;

    private Map<String, QualityMetric> metricsMap;
    private String[] metricKeys = new String[] {};

    private boolean connected;

    private String url;

    public SonarConnection() {
    }

    public void connect(String url) {
        connect(url, null, null);
    }

    @Override
    public void connect(String url, String login, String password) {
        this.url = url;
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize sonar with url " + url);
        }
        if (sonarClient == null) {
            sonarClient = new Sonar(url, login, password);
        }
        connected = true;
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
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projects = new HashMap<SoftwareProjectId, String>();
        try {
            List<Project> names = sonarClient.findProjects().getProjects();
            for (Project project : names) {
                String key = project.getKey();
                projects.put(new SoftwareProjectId(key), project.getName());
            }
        } catch (SonarProjectsNotFoundException e) {
            LOG.warn(e.getMessage(), e);
        }
        return projects;
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
            Resource resource = sonarClient.findResource(artifactId);
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
            if (mavenId != null) {
                Resource resource = sonarClient.findResource(mavenId);
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(resource.getKey());
                return softwareProjectId;
            }
        } catch (SonarResourceNotFoundException e) {
            throw new ProjectNotFoundException("Can't identify project key: " + projectKey, e);
        }
        throw new ProjectNotFoundException("Can't identify project key, there is not enough informations: "
                + projectKey);
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
                Measure itCoverageMeasure = sonarClient.findMeasure(artifactId, "it_coverage");
                if (itCoverageMeasure != null) {
                    Double itCoverage = itCoverageMeasure.getValue();
                    integrationTestResult.setCoverage(itCoverage);
                }
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
            Measure measure = sonarClient.findMeasure(artifactId, key);
            if (measure != null && measure.getValue() != null) {
                SonarQualityMeasure qualityMeasure = QualityMeasures.asQualityMeasure(measure, key);
                qualityMeasure.setName(metricsMap.get(key).getName());
                qualityResult.add(key, asQualityMeasure(qualityMeasure));
            }
        } catch (SonarMeasureNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

    private QualityMeasure asQualityMeasure(SonarQualityMeasure sonarQualityMeasure) {
        QualityMeasure qualityMeasure = new QualityMeasure();
        qualityMeasure.setFormattedValue(sonarQualityMeasure.getFormattedValue());
        qualityMeasure.setKey(sonarQualityMeasure.getKey());
        qualityMeasure.setName(sonarQualityMeasure.getName());
        qualityMeasure.setValue(sonarQualityMeasure.getValue());
        return qualityMeasure;
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarClient.findResource(artifactId);
            return resource.getKey();
        } catch (SonarResourceNotFoundException e) {
            throw new MavenIdNotFoundException("Can't get maven id of software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String artifactId = softwareProjectId.getProjectId();
            Resource resource = sonarClient.findResource(artifactId);
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
        try {
            String artifactId = softwareProjectId.getProjectId();
            sonarClient.findProject(artifactId);
        } catch (SonarProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find if project is disabled, softwareProjectId:"
                    + softwareProjectId + ", url: " + url);
        }
        return false;
    }

    private TestResult createUnitTestAnalysis(String artifactId) {
        TestResult unitTestResult = new TestResult();
        try {
            Double coverage = sonarClient.findMeasure(artifactId, "coverage").getValue();
            Double failures = sonarClient.findMeasure(artifactId, "test_failures").getValue();
            Double errors = sonarClient.findMeasure(artifactId, "test_errors").getValue();
            Double passTests = sonarClient.findMeasure(artifactId, "tests").getValue();

            int skipCount = sonarClient.findMeasure(artifactId, "skipped_tests").getIntValue();
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
            metricsMap = asMetrics(sonarClient.findMetrics());
            Set<String> metricKeysSet = metricsMap.keySet();
            int countMetricKeys = metricKeysSet.size();
            metricKeys = metricKeysSet.toArray(new String[countMetricKeys]);
        } catch (SonarMetricsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't initialize metrics", e);
            }
        }
    }

    private Map<String, QualityMetric> asMetrics(Map<String, SonarQualityMetric> sonarMetrics) {
        Map<String, QualityMetric> metrics = new HashMap<String, QualityMetric>();
        for (Map.Entry<String, SonarQualityMetric> metric : sonarMetrics.entrySet()) {
            String key = metric.getKey();
            SonarQualityMetric value = metric.getValue();
            QualityMetric qualityMetric = asQualityMetric(value);
            metrics.put(key, qualityMetric);
        }
        return metrics;
    }

    private QualityMetric asQualityMetric(SonarQualityMetric sonarQualityMetric) {
        QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setDescription(sonarQualityMetric.getDescription());
        qualityMetric.setDirection(sonarQualityMetric.getDirection());
        qualityMetric.setDomain(sonarQualityMetric.getDomain());
        qualityMetric.setHidden(sonarQualityMetric.getHidden());
        qualityMetric.setKey(sonarQualityMetric.getKey());
        qualityMetric.setName(sonarQualityMetric.getName());
        qualityMetric.setQualitative(sonarQualityMetric.getQualitative());
        qualityMetric.setUserManaged(sonarQualityMetric.getUserManaged());
        qualityMetric.setValTyp(sonarQualityMetric.getValTyp());
        return qualityMetric;
    }

    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, Integer buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        BuildTime buildTime = new BuildTime();
        buildTime.setDuration(1);
        buildTime.setStartTime(new Date());
        return buildTime;
    }

    @Deprecated // NOT USED
    public List<Integer> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return Arrays.asList(1);
    }

    
    @Deprecated // NOT USED
    public State getBuildState(SoftwareProjectId softwareProjectId, Integer buildId)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return State.SUCCESS;
    }

    @Deprecated // NOT USED
    public Date getEstimatedFinishTime(SoftwareProjectId softwareProjectId, Integer buildId)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return new Date();
    }

    @Deprecated // NOT USED
    public boolean isBuilding(SoftwareProjectId softwareProjectId, Integer buildId)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return false;
    }

    @Deprecated // NOT USED
    public int getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return 1;
    }

    @Deprecated // NOT USED
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, Integer buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        return new ArrayList<Commiter>();
    }

    private void checkBuildId(int buildId) {
        Preconditions.checkNotNull(buildId, "buildId is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

}
