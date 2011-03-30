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
