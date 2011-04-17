package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.junit.Test;
import org.sonar.wsclient.services.Measure;

public class MeasureFinderIT {

    @Test
    public void should_find_measure() throws SonarMetricNotFoundException {
        MeasureFinder measureFinder = new MeasureFinder(IntegrationTestData.SONAR_URL);
        measureFinder.init();

        Measure measure = measureFinder.findMeasure(IntegrationTestData.STRUTS_ARTIFACT_ID, "violations_density");

        assertEquals("77.2%", measure.getFormattedValue());
        assertEquals(77.2, measure.getValue(), 0);
    }
}
