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

package net.awired.visuwall.sonarclient;

import static org.apache.commons.lang.StringUtils.isNotBlank;
import java.util.HashMap;
import java.util.Map;
import javax.ws.rs.core.MediaType;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.sonarclient.domain.SonarMetrics;
import net.awired.visuwall.sonarclient.domain.SonarQualityMetric;
import net.awired.visuwall.sonarclient.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.sonarclient.exception.SonarMetricsNotFoundException;
import net.awired.visuwall.sonarclient.exception.SonarResourceNotFoundException;
import net.awired.visuwall.sonarclient.resource.Projects;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SonarClient {

    @VisibleForTesting
    GenericSoftwareClient client;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    @VisibleForTesting
    Sonar sonar;

    private String sonarUrl;

    public SonarClient() {
    }

    public SonarClient(String url) {
        this(url, null, null);
    }

    public SonarClient(String url, String login, String password) {
        Preconditions.checkNotNull(url, "sonarUrl is mandatory");
        Preconditions.checkArgument(!url.isEmpty(), "sonarUrl can't be empty");
        this.sonarUrl = url;
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = Sonar.create(url, login, password);
            client = new GenericSoftwareClient(login, password);
        } else {
            sonar = Sonar.create(url);
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
        if (measure == null) {
            throw new SonarMeasureNotFoundException("Can't find measure key: " + measureKey + ", artifactId: "
                    + artifactId);
        }
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

}
