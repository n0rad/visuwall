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
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.sonarclient.SonarClient;
import net.awired.visuwall.sonarclient.domain.SonarQualityMetric;
import net.awired.visuwall.sonarclient.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.sonarclient.exception.SonarMetricsNotFoundException;
import net.awired.visuwall.sonarclient.resource.Project;
import net.awired.visuwall.sonarclient.resource.Projects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.wsclient.services.Measure;

public class SonarConnectionTest {

    Map<String, SonarQualityMetric> metricList = createMetricList();

    @Mock
    SonarClient sonarClient;

    SonarConnection sonar;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(sonarClient.findMetrics()).thenReturn(metricList);
        sonar = new SonarConnection();
        sonar.sonarClient = sonarClient;
        sonar.connect("http://sonar:9000");
    }

    @Test
    public void should_build_valid_unit_test_result() throws SonarMeasureNotFoundException {
        Measure[] measures = new Measure[5];
        for (Integer i = 0; i < measures.length; i++) {
            measures[i] = new Measure();
            measures[i].setValue(i.doubleValue());
        }

        when(sonarClient.findMeasure("artifactId", "coverage")).thenReturn(measures[0]);
        when(sonarClient.findMeasure("artifactId", "test_failures")).thenReturn(measures[1]);
        when(sonarClient.findMeasure("artifactId", "test_errors")).thenReturn(measures[2]);
        when(sonarClient.findMeasure("artifactId", "tests")).thenReturn(measures[3]);
        when(sonarClient.findMeasure("artifactId", "skipped_tests")).thenReturn(measures[4]);

        SoftwareProjectId projectId = new SoftwareProjectId("artifactId");

        TestResult unitTestResult = sonar.analyzeUnitTests(projectId);

        assertEquals(0, unitTestResult.getCoverage(), 0);
        assertEquals(3, unitTestResult.getFailCount(), 0);
        assertEquals(4, unitTestResult.getSkipCount(), 0);
        assertEquals(0, unitTestResult.getPassCount(), 0);
    }

    @Test
    public void should_build_valid_integration_test_result() throws SonarMeasureNotFoundException {
        Measure measure = new Measure();
        measure.setValue(8D);
        when(sonarClient.findMeasure("artifactId", "it_coverage")).thenReturn(measure);

        SoftwareProjectId projectId = new SoftwareProjectId("artifactId");

        TestResult integrationTestResult = sonar.analyzeIntegrationTests(projectId);

        assertEquals(8, integrationTestResult.getCoverage(), 0);
    }

    /***
     * <metric>
     * <key>generated_lines</key>
     * <name>Generated Lines</name>
     * <description>Number of generated lines</description>
     * <domain>Size</domain>
     * <qualitative>false</qualitative>
     * <direction>-1</direction>
     * <user_managed>false</user_managed>
     * <val_type>INT</val_type>
     * <hidden>false</hidden>
     * </metric>
     */
    @Test
    public void should_build_valid_metric_map() throws Exception {
        SonarQualityMetric generatedLinesMetric = new SonarQualityMetric();
        generatedLinesMetric.setKey("generated_lines");
        generatedLinesMetric.setName("Generated Lines");
        generatedLinesMetric.setDescription("Number of generated lines");
        generatedLinesMetric.setDomain("Size");
        generatedLinesMetric.setQualitative(false);
        generatedLinesMetric.setDirection(-1);
        generatedLinesMetric.setUserManaged(false);
        generatedLinesMetric.setValTyp("INT");
        generatedLinesMetric.setHidden(false);

        Map<String, SonarQualityMetric> qualityMetrics = new HashMap<String, SonarQualityMetric>();
        qualityMetrics.put("size", generatedLinesMetric);
        when(sonarClient.findMetrics()).thenReturn(qualityMetrics);
        sonar.connect("http://sonar:9000");

        Map<String, List<QualityMetric>> metrics = sonar.getMetricsByCategory();
        List<QualityMetric> sizeMetrics = metrics.get("Size");

        QualityMetric generatedLinesMetricTransformed = new QualityMetric();
        generatedLinesMetricTransformed.setKey("generated_lines");
        generatedLinesMetricTransformed.setName("Generated Lines");
        generatedLinesMetricTransformed.setDescription("Number of generated lines");
        generatedLinesMetricTransformed.setDomain("Size");
        generatedLinesMetricTransformed.setQualitative(false);
        generatedLinesMetricTransformed.setDirection(-1);
        generatedLinesMetricTransformed.setUserManaged(false);
        generatedLinesMetricTransformed.setValTyp("INT");
        generatedLinesMetricTransformed.setHidden(false);

        assertEquals(generatedLinesMetricTransformed, sizeMetrics.get(0));
    }

    @Test
    public void should_analyze_quality() throws SonarMeasureNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId("artifactId");

        Measure generatedLines = new Measure();
        generatedLines.setValue(0D);
        when(sonarClient.findMeasure("artifactId", "coverage")).thenReturn(generatedLines);

        QualityResult result = sonar.analyzeQuality(projectId, "coverage");

        assertNotNull(result.getMeasure("coverage"));
    }

    @Test
    public void should_not_fail_if_quality_measures_are_not_found() throws SonarMeasureNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId("artifactId");

        when(sonarClient.findMeasure(anyString(), anyString())).thenThrow(
                new SonarMeasureNotFoundException("not found"));

        sonar.analyzeIntegrationTests(projectId);
    }

    @Test
    public void should_not_fail_if_quality_measures_are_not_found_for_analysis() throws SonarMeasureNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId("artifactId");

        when(sonarClient.findMeasure(anyString(), anyString())).thenThrow(
                new SonarMeasureNotFoundException("not found"));

        sonar.analyzeQuality(projectId);
    }

    @Test
    public void should_not_fail_if_project_id_has_no_artifact_id() {
        SoftwareProjectId projectId = new SoftwareProjectId("");
        sonar.analyzeIntegrationTests(projectId);
    }

    @Test
    public void should_not_return_empty_list_when_finding_project_names() throws Exception {
        Project project = new Project();

        Projects projects = new Projects();
        projects.getProjects().add(project);

        when(this.sonarClient.findProjects()).thenReturn(projects);
        assertFalse(sonar.findProjectNames().isEmpty());
    }

    @Test
    public void should_not_be_equals() {
        SonarConnection s1 = new SonarConnection();
        SonarConnection s2 = new SonarConnection();
        assertFalse(s1.equals(s2));
    }

    @Test
    public void should_not_be_equals_cause_null() {
        SonarConnection s1 = new SonarConnection();
        assertFalse(s1.equals(null));
    }

    @Test
    public void should_close_connection() {
        sonar.close();
        assertTrue(sonar.isClosed());
    }

    @Test
    public void should_not_throw_exception_if_sonar_metrics_are_not_found() throws Exception {
        when(sonarClient.findMetrics()).thenThrow(new SonarMetricsNotFoundException("not found"));
        sonar.connect("http://sonar:9000");
    }

    @Test
    public void should_generated_hash_code() {
        int hashCode = sonar.hashCode();
        assertFalse(hashCode == 0);
    }

    @Test
    public void should_return_false_when_url_is_not_sonar_instance() throws MalformedURLException {
        assertFalse(sonar.isSonarInstance(new URL("http://foo.bar")));
    }

    private Map<String, SonarQualityMetric> createMetricList() {
        Map<String, SonarQualityMetric> metricList = new HashMap<String, SonarQualityMetric>();
        SonarQualityMetric coverageMetric = new SonarQualityMetric();
        coverageMetric.setKey("coverage");
        coverageMetric.setName("Coverage");
        metricList.put(coverageMetric.getKey(), coverageMetric);
        return metricList;
    }

}
