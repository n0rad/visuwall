package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.plugin.sonar.exception.SonarMeasureNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.junit.Test;
import org.sonar.wsclient.services.Measure;

public class QualityMeasuresTest {

    @Test
    public void should_find_quality_measure() throws SonarMetricNotFoundException, SonarMeasureNotFoundException {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        QualityMeasure qualityMeasure = QualityMeasures.asQualityMeasure(coverageMeasure, "measureKey");

        assertEquals("5%", qualityMeasure.getFormattedValue());
        assertEquals(5D, qualityMeasure.getValue(), 0);
        assertEquals("measureKey", qualityMeasure.getKey());
    }

}
