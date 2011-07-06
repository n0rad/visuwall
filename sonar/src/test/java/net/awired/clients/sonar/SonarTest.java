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

package net.awired.clients.sonar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.sonar.domain.SonarMetrics;
import net.awired.clients.sonar.domain.SonarQualityMetric;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.exception.SonarResourceNotFoundException;
import net.awired.clients.sonar.resource.Project;
import net.awired.clients.sonar.resource.Projects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

@SuppressWarnings("unchecked")
public class SonarTest {

    @Mock
    org.sonar.wsclient.Sonar sonar;

    @Mock
    GenericSoftwareClient genericSoftwareClient;

    Sonar sonarClient;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        sonarClient = new Sonar("http://sonar:9000");
        sonarClient.sonar = sonar;
        sonarClient.client = genericSoftwareClient;
    }

    @Test
    public void should_find_measure() throws SonarMeasureNotFoundException {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        Resource resource = Mockito.mock(Resource.class);
        when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

        when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource);

        Measure measure = sonarClient.findMeasure("artifactId", "coverage");

        assertEquals(coverageMeasure.getFormattedValue(), measure.getFormattedValue());
        assertEquals(coverageMeasure.getValue(), measure.getValue());
        assertEquals(coverageMeasure.getMetricName(), measure.getMetricName());
    }

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_sonar_connection_fails() throws SonarMeasureNotFoundException {
        when(sonar.find(Mockito.any(ResourceQuery.class))).thenThrow(new ConnectionException());

        sonarClient.findMeasure("artifactId", "measureKey");
    }

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_resource_is_not_fond() throws SonarMeasureNotFoundException {
        when(sonar.find(Mockito.any(ResourceQuery.class))).thenReturn(null);

        sonarClient.findMeasure("artifactId", "measureKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_artifact_id_is_empty() throws SonarMeasureNotFoundException {
        sonarClient.findMeasure("", "measureKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_sonar_url_is_empty() {
        new Sonar("");
    }

    @Test
    public void should_create_sonar_with_login_and_password() {
        new Sonar("sonarUrl", "login", "password");
    }

    @Test
    public void should_find_metrics() throws Exception {
        SonarQualityMetric qualityMetric = new SonarQualityMetric();
        qualityMetric.setKey("metricKey");

        SonarMetrics sonarMetrics = new SonarMetrics();
        sonarMetrics.metric = new ArrayList<SonarQualityMetric>();
        sonarMetrics.metric.add(qualityMetric);

        when(genericSoftwareClient.resource(anyString(), any(Class.class))).thenReturn(sonarMetrics);

        Map<String, SonarQualityMetric> metrics = sonarClient.findMetrics();
        assertEquals(qualityMetric, metrics.get("metricKey"));
    }

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_measure_is_not_found() throws Exception {
        sonarClient.findMeasure("artifactId", "measureKey");
    }

    @Test
    public void should_return_projects() throws Exception {
        Project project = new Project();
        project.setId(1);
        project.setKey("key");
        project.setName("name");
        project.setQualifier("qualifier");
        project.setScope("scope");

        List<Project> projectList = new ArrayList<Project>();
        projectList.add(project);

        Projects projects = new Projects();
        projects.setProjects(projectList);

        when(genericSoftwareClient.resource(anyString(), any(Class.class), any(MediaType.class)))
                .thenReturn(projects);

        Projects foundProjects = sonarClient.findProjects();
        assertFalse(foundProjects.getProjects().isEmpty());
        Project foundProject = foundProjects.getProjects().get(0);
        assertEquals(1, foundProject.getId().intValue());
        assertEquals("key", foundProject.getKey());
        assertEquals("name", foundProject.getName());
        assertEquals("qualifier", foundProject.getQualifier());
        assertEquals("scope", foundProject.getScope());
    }

    @Test(expected = SonarMetricsNotFoundException.class)
    public void should_throw_exception_if_sonar_metrics_are_not_found() throws Exception {
        Object call = genericSoftwareClient.resource(anyString(), any(Class.class));
        when(call).thenThrow(new ResourceNotFoundException("not found"));

        sonarClient.findMetrics();
    }

    @Test(expected = SonarResourceNotFoundException.class)
    public void should_throw_exception_when_resource_is_not_found() throws Exception {
        sonarClient.findResource("resourceId");
    }

    @Test(expected = SonarResourceNotFoundException.class)
    public void should_throw_exception_when_connection_exception() throws Exception {
        when(sonar.find(any(ResourceQuery.class))).thenThrow(new ConnectionException());
        sonarClient.findResource("resourceId");
    }

    @Test
    public void should_find_resource() throws Exception {
        Resource resource = new Resource();
        when(sonar.find(any(ResourceQuery.class))).thenReturn(resource);
        Resource foundResource = sonarClient.findResource("resourceId");
        assertNotNull(foundResource);
    }
}
