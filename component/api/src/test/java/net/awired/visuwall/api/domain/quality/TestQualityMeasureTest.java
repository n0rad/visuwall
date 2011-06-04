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

package net.awired.visuwall.api.domain.quality;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import org.junit.Test;

public class TestQualityMeasureTest {

    @Test
    public void should_be_equal_when_having_3_values() {
        QualityMeasure qm1 = new QualityMeasure();
        qm1.setFormattedValue("0%");
        qm1.setValue(0D);
        qm1.setName("percent");

        QualityMeasure qm2 = new QualityMeasure();
        qm2.setFormattedValue("0%");
        qm2.setValue(0D);
        qm2.setName("percent");

        assertEquals(qm1, qm2);
    }

    @Test
    public void should_be_equal_when_having_0_value() {
        QualityMeasure qm1 = new QualityMeasure();
        QualityMeasure qm2 = new QualityMeasure();

        assertEquals(qm1, qm2);
    }

    @Test
    public void should_be_equal_when_having_1_value() {
        QualityMeasure qm1 = new QualityMeasure();
        qm1.setName("name");

        QualityMeasure qm2 = new QualityMeasure();
        qm2.setName("name");

        assertEquals(qm1, qm2);
    }

    @Test
    public void should_be_equal_when_having_2_values() {
        QualityMeasure qm1 = new QualityMeasure();
        qm1.setValue(0D);
        qm1.setName("percent");

        QualityMeasure qm2 = new QualityMeasure();
        qm2.setValue(0D);
        qm2.setName("percent");

        assertEquals(qm1, qm2);
    }

    @Test
    public void should_not_be_equal() {
        QualityMeasure qm1 = new QualityMeasure();
        qm1.setValue(1D);
        qm1.setName("percent");

        QualityMeasure qm2 = new QualityMeasure();
        qm2.setValue(0D);
        qm2.setName("percent");

        assertFalse(qm1.equals(qm2));
    }

}
