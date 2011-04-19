package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.Map;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.junit.Test;


public class MetricsLoaderTest {

    @Test
    public void should_create_metrics() throws SonarMetricsNotFoundException {
        final QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setKey("coverage_key");

        MetricsLoader metricsLoader = new MetricsLoader(){
            @Override
            SonarMetrics fetchMetrics(String sonarUrl) {
                SonarMetrics sonarMetrics = new SonarMetrics();

                sonarMetrics.metric = new ArrayList<QualityMetric>();
                sonarMetrics.metric.add(qualityMetric);

                return sonarMetrics;
            }
        };

        Map<String, QualityMetric> metrics = metricsLoader.createMetricList("sonarUrl");

        QualityMetric qm = metrics.get("coverage_key");
        assertEquals(qualityMetric, qm);
    }
}
