package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

public class MeasureFinderTest {

    MetricsLoader metricsLoader = Mockito.mock(MetricsLoader.class);

    @Before
    public void init() throws SonarMetricsNotFoundException {
        QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setName("Coverage");
        qualityMetric.setKey("coverage");

        Map<String, QualityMetric> metricList = new HashMap<String, QualityMetric>();
        metricList.put("coverage", qualityMetric );

        when(metricsLoader.createMetricList(Matchers.anyString())).thenReturn(metricList);
    }

    @Test
    public void testCreateQualityMeasure() throws SonarMetricNotFoundException {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        Resource resource = Mockito.mock(Resource.class);
        when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

        Sonar sonar = Mockito.mock(Sonar.class);
        when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource);

        Map<String, QualityMetric> metrics = new HashMap<String, QualityMetric>();
        QualityMetric qualityMetric = new QualityMetric();
        qualityMetric.setName("Coverage");
        metrics.put("coverage", qualityMetric);

        MeasureFinder measureFinder = new MeasureFinder("sonarUrl");
        measureFinder.metricsLoader = metricsLoader;
        measureFinder.sonar = sonar;
        measureFinder.metrics = metrics;

        QualityMeasure qualityMeasure = measureFinder.createQualityMeasure("projectId", "coverage");

        assertEquals(coverageMeasure.getFormattedValue(), qualityMeasure.getFormattedValue());
        assertEquals(coverageMeasure.getValue(), qualityMeasure.getValue());
        assertEquals(coverageMeasure.getMetricName(), qualityMeasure.getName());
    }

    @Test
    public void testFindMeasure() throws SonarMetricNotFoundException {
        Measure coverageMeasure = new Measure();
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        coverageMeasure.setMetricName("Coverage");

        Resource resource = Mockito.mock(Resource.class);
        when(resource.getMeasure(Matchers.anyString())).thenReturn(coverageMeasure);

        Sonar sonar = Mockito.mock(Sonar.class);
        when(sonar.find((ResourceQuery) Matchers.anyObject())).thenReturn(resource );

        MeasureFinder measureFinder = new MeasureFinder("sonarUrl");
        measureFinder.metricsLoader = metricsLoader;
        measureFinder.sonar = sonar;

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        Measure measure = measureFinder.findMeasure("artifactId", "coverage");

        assertEquals(coverageMeasure.getFormattedValue(), measure.getFormattedValue());
        assertEquals(coverageMeasure.getValue(), measure.getValue());
        assertEquals(coverageMeasure.getMetricName(), measure.getMetricName());
    }

    @Test
    public void should_get_all_metric_keys() {
        Map<String, QualityMetric> metrics = new TreeMap<String, QualityMetric>();
        metrics.put("coverage", new QualityMetric());
        metrics.put("violations", new QualityMetric());

        MeasureFinder measureFinder = new MeasureFinder("sonarUrl");
        measureFinder.metrics = metrics;

        String[] metricKeys = measureFinder.getAllMetricKeys();

        assertArrayEquals(new String[]{"coverage", "violations"}, metricKeys);
    }

}
