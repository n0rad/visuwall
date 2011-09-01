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
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.awired.clients.common.Tests;
import net.awired.clients.sonar.domain.SonarQualityMetric;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.resource.Project;
import net.awired.clients.sonar.resource.Projects;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;

@RunWith(Parameterized.class)
public class SonarITest {

    private Sonar sonar;

    @Parameters
    public static Collection<Object[]> createParameters() {
        return Tests.createUrlInstanceParametersFromProperty("sonarInstances");
    }

    public SonarITest(String sonarUrl) {
        System.out.println("Testing Sonar url:'" + sonarUrl + "'");
        sonar = new Sonar(sonarUrl);
    }

    @Test
    public void should_find_measure() throws SonarMeasureNotFoundException {
        Measure measure = sonar.findMeasure("org.apache.struts:struts-parent", "violations_density");
        assertTrue(measure.getFormattedValue().length() > 0);
        assertTrue(measure.getValue() > 0);
    }

    @Test
    public void should_find_projects() throws Exception {
        Projects projects = sonar.findProjects();
        List<Project> projectList = projects.getProjects();
        assertFalse(projectList.isEmpty());
    }

    @Test
    public void should_find_resource_by_id() throws Exception {
        Integer resourceId = sonar.findProjects().getProjects().get(0).getId();
        Resource resource = sonar.findResource(resourceId.toString());
        assertNotNull(resource);
    }

    @Test
    public void should_find_metrics() throws SonarMetricsNotFoundException {
        Map<String, SonarQualityMetric> metrics = sonar.findMetrics();

        SonarQualityMetric metric = metrics.get("function_complexity");
        System.out.println(metric);

        assertEquals("Complexity average by method", metric.getDescription());
        assertEquals(-1, metric.getDirection().intValue());
        assertEquals("Complexity", metric.getDomain());
        assertFalse(metric.getHidden());
        assertEquals("function_complexity", metric.getKey());
        assertEquals("Complexity /method", metric.getName());
        assertTrue(metric.getQualitative());
        assertNull(metric.getUserManaged());
        assertNull(metric.getValTyp());
    }

    @Test
    public void should_find_project() throws Exception {
        Projects projects = sonar.findProjects();
        List<Project> projectList = projects.getProjects();
        Project project = projectList.get(0);

        Project foundProject = sonar.findProject(project.getKey().toString());

        assertEquals(project, foundProject);
        assertNotNull(foundProject.getId());
        assertNotNull(foundProject.getKey());
        assertNotNull(foundProject.getName());
        assertNotNull(foundProject.getQualifier());
        assertNotNull(foundProject.getScope());
    }

}
