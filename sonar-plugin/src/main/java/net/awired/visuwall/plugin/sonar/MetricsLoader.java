package net.awired.visuwall.plugin.sonar;

import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricsNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;

public class MetricsLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsLoader.class);

    public Map<String, QualityMetric> createMetricList(String sonarUrl) throws SonarMetricsNotFoundException {
        SonarMetrics sonarMetrics = fetchMetrics(sonarUrl);

        Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();
        for (QualityMetric metric:sonarMetrics.metric) {
            qualityMetrics.put(metric.getKey(), metric);
        }
        return qualityMetrics;
    }

    @VisibleForTesting
    SonarMetrics fetchMetrics(String sonarUrl) throws SonarMetricsNotFoundException {
        String metricUrl = sonarUrl+"/api/metrics?format=xml";
        try {
            InputStream xmlStream = new URL(metricUrl).openStream();
            Unmarshaller unmarshaller = JAXBContext.newInstance(SonarMetrics.class).createUnmarshaller();
            SonarMetrics sonarMetrics = SonarMetrics.class.cast(unmarshaller.unmarshal(xmlStream));
            return sonarMetrics;
        } catch (Exception e) {
            String msg = "metric can't be found at url: "+metricUrl;
            LOG.error(msg,e);
            throw new SonarMetricsNotFoundException(msg, e);
        }
    }

}
