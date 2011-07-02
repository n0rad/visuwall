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

import static org.apache.commons.lang.StringUtils.isNotBlank;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarResourceNotFoundException;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.base.Strings;

public class SonarFinder {

    private String sonarUrl;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    @VisibleForTesting
    Sonar sonar;

    public SonarFinder(String sonarUrl) {
        this(sonarUrl, null, null);
    }

    public SonarFinder(String sonarUrl, String login, String password) {
        Preconditions.checkNotNull(sonarUrl, "sonarUrl is mandatory");
        Preconditions.checkArgument(!sonarUrl.isEmpty(), "sonarUrl can't be empty");
        this.sonarUrl = sonarUrl;
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = Sonar.create(sonarUrl, login, password);
        } else {
            sonar = Sonar.create(sonarUrl);
        }
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

}
