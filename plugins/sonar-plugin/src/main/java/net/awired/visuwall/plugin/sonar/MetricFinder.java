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

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import com.google.common.annotations.VisibleForTesting;

public class MetricFinder {

    private String sonarUrl;

    @VisibleForTesting
    GenericSoftwareClient genericSoftwareClient;

    public MetricFinder(String sonarUrl) {
        this.sonarUrl = sonarUrl;
        this.genericSoftwareClient = new GenericSoftwareClient();
    }

    public Map<String, QualityMetric> findMetrics() throws SonarMetricsNotFoundException {
        try {
            String metricUrl = sonarUrl + "/api/metrics?format=xml";
            SonarMetrics sonarMetrics = genericSoftwareClient.resource(metricUrl, SonarMetrics.class);
            Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();
            for (QualityMetric metric : sonarMetrics.metric) {
                qualityMetrics.put(metric.getKey(), metric);
            }
            return qualityMetrics;
        } catch (ResourceNotFoundException e) {
            throw new SonarMetricsNotFoundException("Can't find sonar metrics with Sonar: " + sonarUrl, e);
        }
    }

}
