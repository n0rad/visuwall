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

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.wsclient.services.Measure;

public class SonarConnectionTest {

    Map<String, QualityMetric> metricList = createMetricList();

    MetricFinder metricFinder = Mockito.mock(MetricFinder.class);
    MeasureFinder measureFinder = Mockito.mock(MeasureFinder.class);

    SonarConnection sonar;
    @Before
    public void init() throws Exception {
        when(metricFinder.findMetrics()).thenReturn(metricList);
        sonar = new SonarConnection();
        sonar.measureFinder = measureFinder;
        sonar.metricFinder = metricFinder;
        sonar.connect("http://sonar:9000");
    }

    @Test
    public void should_find_project() throws SonarMeasureNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        when(measureFinder.findMeasure("artifactId", "comment_blank_line")).thenReturn(new Measure());

        assertTrue(sonar.contains(projectId));
    }

    @Test
    public void should_not_find_project() throws SonarMeasureNotFoundException {
        when(measureFinder.findMeasure(Matchers.anyString(), Matchers.anyString())).thenThrow(
                new SonarMeasureNotFoundException(""));

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        assertFalse(sonar.contains(projectId));
    }

    @Test
    public void should_return_false_when_no_artifact_id_found() {

        assertFalse(sonar.contains(new ProjectId()));
    }

    @Test
    public void should_build_valid_unit_test_result() throws SonarMeasureNotFoundException {
        Measure[] measures = new Measure[5];
        for (Integer i = 0; i < measures.length; i++) {
            measures[i] = new Measure();
            measures[i].setValue(i.doubleValue());
        }

        when(measureFinder.findMeasure("artifactId", "coverage")).thenReturn(measures[0]);
        when(measureFinder.findMeasure("artifactId", "test_failures")).thenReturn(measures[1]);
        when(measureFinder.findMeasure("artifactId", "test_errors")).thenReturn(measures[2]);
        when(measureFinder.findMeasure("artifactId", "tests")).thenReturn(measures[3]);
        when(measureFinder.findMeasure("artifactId", "skipped_tests")).thenReturn(measures[4]);

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

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
        when(measureFinder.findMeasure("artifactId", "it_coverage")).thenReturn(measure);

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        TestResult integrationTestResult = sonar.analyzeIntegrationTests(projectId);

        assertEquals(8, integrationTestResult.getCoverage(), 0);
    }

    /***
     * <metric>
    	<key>generated_lines</key>
    	<name>Generated Lines</name>
    	<description>Number of generated lines</description>
    	<domain>Size</domain>
    	<qualitative>false</qualitative>
    	<direction>-1</direction>
    	<user_managed>false</user_managed>
    	<val_type>INT</val_type>
    	<hidden>false</hidden>
    </metric>
     */
    @Test
    public void should_build_valid_metric_map() throws Exception {
        QualityMetric generatedLinesMetric = new QualityMetric();
        generatedLinesMetric.setKey("generated_lines");
        generatedLinesMetric.setName("Generated Lines");
        generatedLinesMetric.setDescription("Number of generated lines");
        generatedLinesMetric.setDomain("Size");
        generatedLinesMetric.setQualitative(false);
        generatedLinesMetric.setDirection(-1);
        generatedLinesMetric.setUserManaged(false);
        generatedLinesMetric.setValTyp("INT");
        generatedLinesMetric.setHidden(false);

        Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();
        qualityMetrics.put("size", generatedLinesMetric);
        when(metricFinder.findMetrics()).thenReturn(qualityMetrics);
        sonar.connect("http://sonar:9000");

        Map<String, List<QualityMetric>> metrics = sonar.getMetricsByCategory();
        List<QualityMetric> sizeMetrics = metrics.get("Size");

        assertEquals(generatedLinesMetric, sizeMetrics.get(0));
    }

    @Test
    public void should_analyze_quality() throws SonarMeasureNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        Measure generatedLines = new Measure();
        generatedLines.setValue(0D);
        when(measureFinder.findMeasure("artifactId", "coverage")).thenReturn(generatedLines);

        QualityResult result = sonar.analyzeQuality(projectId, "coverage");
        
        assertNotNull(result.getMeasure("coverage"));
    }

    @Test
    public void should_not_fail_if_quality_measures_are_not_found() throws SonarMeasureNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");
        
        when(measureFinder.findMeasure(anyString(), anyString())).thenThrow(
                new SonarMeasureNotFoundException("not found"));

        sonar.analyzeIntegrationTests(projectId);
    }

    @Test
    public void should_not_fail_if_quality_measures_are_not_found_for_analysis() throws SonarMeasureNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        when(measureFinder.findMeasure(anyString(), anyString())).thenThrow(
                new SonarMeasureNotFoundException("not found"));

        sonar.analyzeQuality(projectId);
    }

    @Test
    public void should_not_fail_if_project_id_has_no_artifact_id() {
        ProjectId projectId = new ProjectId();
        sonar.analyzeIntegrationTests(projectId);
    }

    @Test
    public void should_return_empty_list_when_finding_all_projects() {
        assertTrue(sonar.findAllProjects().isEmpty());
    }

    @Test
    public void should_return_empty_list_when_finding_projects_by_names() {
        assertTrue(sonar.findProjectIdsByNames(null).isEmpty());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_project() throws ProjectNotFoundException {
        sonar.findProject(null);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_find_project_by_project_id() throws ProjectNotFoundException {
        sonar.findProject(null);
    }

    @Test
    public void should_return_empty_list_when_finding_project_names() {
        assertTrue(sonar.findProjectNames().isEmpty());
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
    }

    @Test(expected = ConnectionException.class)
    public void should_throw_exception_if_sonar_metrics_are_not_found() throws Exception {
        when(metricFinder.findMetrics()).thenThrow(new SonarMetricsNotFoundException("not found"));
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

    private Map<String, QualityMetric> createMetricList() {
        Map<String, QualityMetric> metricList = new HashMap<String, QualityMetric>();
        QualityMetric coverageMetric = new QualityMetric();
        coverageMetric.setKey("coverage");
        coverageMetric.setName("Coverage");
        metricList.put(coverageMetric.getKey(), coverageMetric);
        return metricList;
    }

}
