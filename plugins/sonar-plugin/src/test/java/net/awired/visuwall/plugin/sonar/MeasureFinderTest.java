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
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

public class MeasureFinderTest {

    @Mock
    Sonar sonar;

    MeasureFinder measureFinder;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        measureFinder = new MeasureFinder("http://sonar:9000");
        measureFinder.sonar = sonar;
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

        Measure measure = measureFinder.findMeasure("artifactId", "coverage");

        assertEquals(coverageMeasure.getFormattedValue(), measure.getFormattedValue());
        assertEquals(coverageMeasure.getValue(), measure.getValue());
        assertEquals(coverageMeasure.getMetricName(), measure.getMetricName());
    }

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_sonar_connection_fails() throws SonarMeasureNotFoundException {
        when(sonar.find(Mockito.any(ResourceQuery.class))).thenThrow(new ConnectionException());

        measureFinder.findMeasure("artifactId", "measureKey");
    }

    @Test(expected = SonarMeasureNotFoundException.class)
    public void should_throw_exception_when_resource_is_not_fond() throws SonarMeasureNotFoundException {
        when(sonar.find(Mockito.any(ResourceQuery.class))).thenReturn(null);

        measureFinder.findMeasure("artifactId", "measureKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_artifact_id_is_empty() throws SonarMeasureNotFoundException {
        measureFinder.findMeasure("", "measureKey");
    }

    @Test(expected = IllegalArgumentException.class)
    public void should_throw_exception_when_sonar_url_is_empty() {
        new MeasureFinder("");
    }

    @Test
    public void should_create_sonar_with_login_and_password() {
        new MeasureFinder("sonarUrl", "login", "password");
    }
}
