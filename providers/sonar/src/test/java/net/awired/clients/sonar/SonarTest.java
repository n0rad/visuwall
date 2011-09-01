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

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import javax.ws.rs.core.MediaType;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.exception.SonarProjectNotFoundException;
import net.awired.clients.sonar.exception.SonarProjectsNotFoundException;
import net.awired.clients.sonar.exception.SonarResourceNotFoundException;
import net.awired.clients.sonar.resource.Projects;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.wsclient.connectors.ConnectionException;
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

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_measure_is_not_found() throws Exception {
        sonarClient.findMeasure("artifactId", "measureKey");
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

    @Test(expected = SonarProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Projects projects = new Projects();
        when(genericSoftwareClient.resource(anyString(), eq(Projects.class), eq(MediaType.APPLICATION_XML_TYPE)))
                .thenReturn(projects);
        sonarClient.findProject("");
    }

    @Test(expected = SonarProjectsNotFoundException.class)
    public void should_throw_exception_when_projects_are_not_found() throws Exception {
        Throwable exception = new ResourceNotFoundException("");
        when(genericSoftwareClient.resource(anyString(), eq(Projects.class), eq(MediaType.APPLICATION_XML_TYPE)))
                .thenThrow(exception);
        sonarClient.findProjects();
    }

    @Test(expected = SonarProjectNotFoundException.class)
    public void should_throw_exception_when_projects_are_not_found_and_searching_for_a_project() throws Exception {
        Throwable exception = new ResourceNotFoundException("");
        when(genericSoftwareClient.resource(anyString(), eq(Projects.class), eq(MediaType.APPLICATION_XML_TYPE)))
                .thenThrow(exception);
        sonarClient.findProject("");
    }
}
