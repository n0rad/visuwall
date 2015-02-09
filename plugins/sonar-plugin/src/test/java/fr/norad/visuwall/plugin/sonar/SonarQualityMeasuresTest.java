/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import org.sonar.wsclient.services.Measure;

public class SonarQualityMeasuresTest {

    @Test
    public void should_find_quality_measure() {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        SonarQualityMeasure qualityMeasure = QualityMeasures.asQualityMeasure(coverageMeasure, "measureKey");

        assertEquals("5%", qualityMeasure.getFormattedValue());
        assertEquals(5D, qualityMeasure.getValue(), 0);
        assertEquals("measureKey", qualityMeasure.getKey());
    }

}
