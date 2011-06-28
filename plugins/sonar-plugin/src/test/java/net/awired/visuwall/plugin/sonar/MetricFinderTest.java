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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Map;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;
import org.junit.Test;
import org.mockito.Mockito;

public class MetricFinderTest {

    @Test
    public void should_find_metrics() throws Exception {
        QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setKey("metricKey");

        SonarMetrics sonarMetrics = new SonarMetrics();
        sonarMetrics.metric = new ArrayList<QualityMetric>();
        sonarMetrics.metric.add(qualityMetric);

        GenericSoftwareClient genericSoftwareClient = Mockito.mock(GenericSoftwareClient.class);
        when(genericSoftwareClient.resource(anyString(), any(Class.class))).thenReturn(sonarMetrics);

        MetricFinder metricFinder = new MetricFinder("http://sonar:9000");
        metricFinder.genericSoftwareClient = genericSoftwareClient;

        Map<String, QualityMetric> metrics = metricFinder.findMetrics();
        assertEquals(qualityMetric, metrics.get("metricKey"));
    }

    @Test(expected = SonarMetricsNotFoundException.class)
    public void should_throw_exception_if_sonar_metrics_are_not_found() throws Exception {
        GenericSoftwareClient genericSoftwareClient = Mockito.mock(GenericSoftwareClient.class);
        Object call = genericSoftwareClient.resource(anyString(), any(Class.class));
        when(call).thenThrow(new ResourceNotFoundException("not found"));

        MetricFinder metricFinder = new MetricFinder("http://sonar:9000");
        metricFinder.genericSoftwareClient = genericSoftwareClient;

        metricFinder.findMetrics();
    }

}
