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
import static org.mockito.Mockito.when;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

public class MeasureFinderTest {

    @Test
    public void should_find_measure() throws SonarMetricNotFoundException, SonarMeasureNotFoundException {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        Resource resource = Mockito.mock(Resource.class);
        when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

        Sonar sonar = Mockito.mock(Sonar.class);
        when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource);

        MeasureFinder measureFinder = new MeasureFinder("http://sonar:9000");
        measureFinder.sonar = sonar;
        Measure measure = measureFinder.findMeasure("artifactId", "coverage");

        assertEquals(coverageMeasure.getFormattedValue(), measure.getFormattedValue());
        assertEquals(coverageMeasure.getValue(), measure.getValue());
        assertEquals(coverageMeasure.getMetricName(), measure.getMetricName());
    }

}
