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

import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.sonar.domain.SonarMetrics;
import net.awired.clients.sonar.domain.SonarQualityMetric;
import net.awired.clients.sonar.exception.SonarMeasureNotFoundException;
import net.awired.clients.sonar.exception.SonarMetricsNotFoundException;
import net.awired.clients.sonar.exception.SonarProjectNotFoundException;
import net.awired.clients.sonar.exception.SonarResourceNotFoundException;
import net.awired.clients.sonar.resource.Project;
import net.awired.clients.sonar.resource.Projects;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class Sonar {

    @VisibleForTesting
    GenericSoftwareClient client;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    @VisibleForTesting
    org.sonar.wsclient.Sonar sonar;

    private String sonarUrl;

    public Sonar(String url) {
        this(url, null, null);
    }

    public Sonar(String url, String login, String password) {
        Preconditions.checkNotNull(url, "sonarUrl is mandatory");
        Preconditions.checkArgument(!url.isEmpty(), "sonarUrl can't be empty");
        this.sonarUrl = url;
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = org.sonar.wsclient.Sonar.create(url, login, password);
            client = new GenericSoftwareClient(login, password);
        } else {
            sonar = org.sonar.wsclient.Sonar.create(url);
            client = new GenericSoftwareClient();
        }
    }

    public Projects findProjects() throws ResourceNotFoundException {
        String projectsUrl = sonarUrl + "/api/projects";
        Projects projects = client.resource(projectsUrl, Projects.class, MediaType.APPLICATION_XML_TYPE);
        return projects;
    }

    public Measure findMeasure(String artifactId, String measureKey) throws SonarMeasureNotFoundException {
        Preconditions.checkArgument(!Strings.isNullOrEmpty(artifactId), "artifactId is a mandatory parameter");
        Preconditions.checkNotNull(measureKey, "measureKey is a mandatory parameter");
        Measure measure = findMeasureFromSonar(artifactId, measureKey);
        return measure;
    }

    private Measure findMeasureFromSonar(String artifactId, String measureKey) throws SonarMeasureNotFoundException {
        ResourceQuery query = ResourceQuery.createForMetrics(artifactId, measureKey);
        try {
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarMeasureNotFoundException("Metric " + measureKey + " not found for project "
                        + artifactId + " in Sonar " + sonarUrl);
            }
            return resource.getMeasure(measureKey);
        } catch (ConnectionException e) {
            throw new SonarMeasureNotFoundException("Metric " + measureKey + " not found for project " + artifactId
                    + " in Sonar " + sonarUrl, e);
        }
    }

    public Resource findResource(String resourceId) throws SonarResourceNotFoundException {
        Preconditions.checkNotNull(resourceId, "resourceId is mandatory");
        ResourceQuery query = new ResourceQuery(resourceId);
        try {
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarResourceNotFoundException("Resource " + resourceId + " not found in Sonar " + sonarUrl);
            }
            return resource;
        } catch (ConnectionException e) {
            throw new SonarResourceNotFoundException("Resource " + resourceId + " not found in Sonar " + sonarUrl, e);

        }
    }

    public Map<String, SonarQualityMetric> findMetrics() throws SonarMetricsNotFoundException {
        try {
            String metricUrl = sonarUrl + "/api/metrics?format=xml";
            SonarMetrics sonarMetrics = client.resource(metricUrl, SonarMetrics.class);
            Map<String, SonarQualityMetric> qualityMetrics = new HashMap<String, SonarQualityMetric>();
            for (SonarQualityMetric metric : sonarMetrics.metric) {
                qualityMetrics.put(metric.getKey(), metric);
            }
            return qualityMetrics;
        } catch (ResourceNotFoundException e) {
            throw new SonarMetricsNotFoundException("Can't find sonar metrics with Sonar: " + sonarUrl, e);
        }
    }

    public Project findProject(String resourceId) throws SonarProjectNotFoundException {
        Preconditions.checkNotNull(resourceId);
        try {
            List<Project> projects = findProjects().getProjects();
            for (Project project : projects) {
                if (project.getKey().equals(resourceId)) {
                    return project;
                }
            }
            throw new SonarProjectNotFoundException("Can't find Sonar project with resourceId: '" + resourceId + "'");
        } catch (ResourceNotFoundException e) {
            throw new SonarProjectNotFoundException("Can't find Sonar project with resourceId: '" + resourceId + "'",
                    e);
        }
    }

}
