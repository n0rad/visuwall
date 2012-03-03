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

import java.net.URL;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.clients.sonar.Sonar;
import net.awired.clients.sonar.domain.SonarQualityMetric;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.exception.SonarResourceNotFoundException;
import net.awired.clients.sonar.resource.Project;
import net.awired.clients.sonar.resource.Projects;
import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

public class SonarConnectionTest {

    Map<String, SonarQualityMetric> metricList = createMetricList();

    @Mock
    Sonar sonarClient;

    SonarConnection sonar;

    @Before
    public void init() throws Exception {
        MockitoAnnotations.initMocks(this);
        when(sonarClient.findMetrics()).thenReturn(metricList);
        sonar = new SonarConnection();
        sonar.sonarClient = sonarClient;
        sonar.connect("http://sonar:9000", "", "");
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
     * <metric> <key>generated_lines</key> <name>Generated Lines</name> <description>Number of generated
     * lines</description> <domain>Size</domain> <qualitative>false</qualitative> <direction>-1</direction>
     * <user_managed>false</user_managed> <val_type>INT</val_type> <hidden>false</hidden> </metric>
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
        sonar.connect("http://sonar:9000", "", "");

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
        when(sonarClient.findMetrics()).thenThrow(new SonarMetricsNotFoundException("not found", null));
        sonar.connect("http://sonar:9000", "", "");
    }

    @Test
    public void should_generated_hash_code() {
        int hashCode = sonar.hashCode();
        assertFalse(hashCode == 0);
    }

    @Test
    public void projects_should_never_be_disabled() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = sonar.isProjectDisabled(softwareProjectId);
        assertFalse(isDisabled);
    }

    @Test
    public void build_numbers_should_not_be_empty() throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        List<Integer> buildIds = sonar.getBuildIds(softwareProjectId);
        assertFalse(buildIds.isEmpty());
    }

    @Test
    public void build_state_should_always_be_success() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        BuildState state = sonar.getBuildState(softwareProjectId, 1);
        assertEquals(BuildState.SUCCESS, state);
    }

    @Test
    public void build_time_should_always_be_set() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        BuildTime buildTime = sonar.getBuildTime(softwareProjectId, 1);
        assertNotNull(buildTime);
        assertNotNull(buildTime.getStartTime());
        assertTrue(buildTime.getDuration() > 0);
    }

    @Test
    public void builds_should_never_be_building() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isBuilding = sonar.isBuilding(softwareProjectId, 1);
        assertFalse(isBuilding);
    }

    @Test
    public void last_build_number_should_always_be_1() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        int lastBuildId = sonar.getLastBuildId(softwareProjectId);
        assertEquals(1, lastBuildId);
    }

    @Test
    public void commiter_list_should_always_be_empty() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        List<Commiter> commiters = sonar.getBuildCommiters(softwareProjectId, 1);
        assertTrue(commiters.isEmpty());
    }

    @Test
    public void estimated_finish_time_must_not_be_null() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        Date estimatedFinishTime = sonar.getEstimatedFinishTime(softwareProjectId, 1);
        assertNotNull(estimatedFinishTime);
    }

    @Test
    public void should_get_maven_id() throws Exception {
        Resource resource = new Resource();
        resource.setKey("artifactId");
        when(sonarClient.findResource(anyString())).thenReturn(resource);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String mavenId = sonar.getMavenId(softwareProjectId);

        assertEquals("artifactId", mavenId);
    }

    @Test
    public void should_get_project_name() throws Exception {
        Resource resource = new Resource();
        resource.setName("name");
        when(sonarClient.findResource(anyString())).thenReturn(resource);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String name = sonar.getName(softwareProjectId);

        assertEquals("name", name);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_name_of_inexistant_project() throws Exception {
        Throwable notFound = new SonarResourceNotFoundException("not found");
        when(sonarClient.findResource(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        sonar.getName(softwareProjectId);
    }

    @Test
    public void should_get_project_description() throws Exception {
        Resource resource = new Resource();
        resource.setLongName("description");
        when(sonarClient.findResource(anyString())).thenReturn(resource);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        String description = sonar.getDescription(softwareProjectId);

        assertEquals("description", description);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_description_of_inexistant_project() throws Exception {
        Throwable notFound = new SonarResourceNotFoundException("not found");
        when(sonarClient.findResource(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        sonar.getDescription(softwareProjectId);
    }

    @Test(expected = MavenIdNotFoundException.class)
    public void should_throw_exception_when_searching_maven_id_of_inexistant_project() throws Exception {
        Throwable notFound = new SonarResourceNotFoundException("not found");
        when(sonarClient.findResource(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        sonar.getMavenId(softwareProjectId);
    }

    @Test
    public void should_find_all_software_project_ids() throws Exception {
        Project project1 = new Project();
        project1.setName("name1");
        project1.setKey("key1");
        Project project2 = new Project();
        project2.setName("name2");
        project2.setKey("key2");

        Projects projects = new Projects();
        projects.getProjects().add(project1);
        projects.getProjects().add(project2);

        when(sonarClient.findProjects()).thenReturn(projects);

        Map<SoftwareProjectId, String> softwareProjectIds = sonar.listSoftwareProjectIds();

        assertEquals("name1", softwareProjectIds.get(new SoftwareProjectId("key1")));
        assertEquals("name2", softwareProjectIds.get(new SoftwareProjectId("key2")));
    }

    @Test
    public void should_identify_project_with_maven_id() throws Exception {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setMavenId("groupId:artifactId");

        Resource resource = new Resource();
        resource.setKey("groupId:artifactId");
        when(sonarClient.findResource("groupId:artifactId")).thenReturn(resource);

        SoftwareProjectId softwareProjectId = sonar.identify(projectKey);

        assertEquals("groupId:artifactId", softwareProjectId.getProjectId());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_identifying_inexistant_project() throws Exception {
        Throwable notFound = new SonarResourceNotFoundException("not found");
        when(sonarClient.findResource(anyString())).thenThrow(notFound);

        ProjectKey projectKey = new ProjectKey();
        sonar.identify(projectKey);
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_not_fail_if_url_is_not_manageable() throws Exception {
        SonarPlugin sonarPlugin = new SonarPlugin();
        String url = "http://www.google.fr";
        sonarPlugin.getSoftwareId(new URL(url), null);
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
