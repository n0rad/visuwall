package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class SonarPluginTest {

    Map<String, QualityMetric> metricList = createMetricList();

    MetricsLoader metricsLoader = Mockito.mock(MetricsLoader.class);
    MeasureFinder measureCreator = Mockito.mock(MeasureFinder.class);

    @Before
    public void init() {
        when(metricsLoader.createMetricList(Matchers.anyString())).thenReturn(metricList);
        when(measureCreator.getAllMetricKeys()).thenReturn(new String[]{"coverage"});
    }

    @Test
    public void should_create_quality_measure() throws SonarMetricNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        QualityMeasure coverageMeasure = new QualityMeasure();
        coverageMeasure.setName("Coverage");
        coverageMeasure.setFormattedValue("5%");
        coverageMeasure.setValue(5D);
        when(measureCreator.createQualityMeasure(projectId.getArtifactId(), "coverage")).thenReturn(coverageMeasure);

        SonarPlugin sonarPlugin = new SonarPlugin();
        sonarPlugin.setMeasureCreator(measureCreator);

        QualityResult qualityResult = sonarPlugin.populateQuality(projectId);
        QualityMeasure freshCoverageMeasure = qualityResult.getMeasure("coverage");

        assertEquals(coverageMeasure.getName(), freshCoverageMeasure.getName());
        assertEquals(coverageMeasure.getValue(), freshCoverageMeasure.getValue());
        assertEquals(coverageMeasure.getFormattedValue(), freshCoverageMeasure.getFormattedValue());
    }

    @Test
    public void should_find_project() {
        SonarPlugin sonarPlugin = new SonarPlugin();
        sonarPlugin.setMeasureCreator(measureCreator);

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        assertTrue(sonarPlugin.contains(projectId));
    }

    @Test
    public void should_not_find_project() throws SonarMetricNotFoundException {
        SonarPlugin sonarPlugin = new SonarPlugin();
        sonarPlugin.setMeasureCreator(measureCreator);

        when(measureCreator.findMeasure(Matchers.anyString(), Matchers.anyString())).thenThrow(new SonarMetricNotFoundException(""));

        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("artifactId");

        assertFalse(sonarPlugin.contains(projectId));
    }

    @Test
    public void should_return_false_when_no_artifact_id_found() {
        SonarPlugin sonarPlugin = new SonarPlugin();
        assertFalse(sonarPlugin.contains(new ProjectId()));
    }

    private Map<String, QualityMetric> createMetricList() {
        Map<String, QualityMetric> metricList = new HashMap<String, QualityMetric>();
        QualityMetric coverageMetric = new QualityMetric();
        coverageMetric.setKey("coverage");
        coverageMetric.setName("Coverage");
        metricList.put(coverageMetric.getKey(), coverageMetric);
        return metricList;
    }

}
