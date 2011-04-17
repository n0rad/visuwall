package net.awired.visuwall.plugin.sonar;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.domain.SonarMetrics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MetricsLoader {

    private static final Logger LOG = LoggerFactory.getLogger(MetricsLoader.class);

    public Map<String, QualityMetric> createMetricList(String sonarUrl) {
        Map<String, QualityMetric> qualityMetrics = new HashMap<String, QualityMetric>();

        String metricUrl = sonarUrl+"/api/metrics?format=xml";
        try {
            InputStream xmlStream = new URL(metricUrl).openStream();
            Unmarshaller unmarshaller = JAXBContext.newInstance(SonarMetrics.class).createUnmarshaller();
            SonarMetrics sonarMetrics = SonarMetrics.class.cast(unmarshaller.unmarshal(xmlStream));
            for (QualityMetric metric:sonarMetrics.metric) {
                qualityMetrics.put(metric.getKey(), metric);
            }
            return qualityMetrics;
        } catch (MalformedURLException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        } catch (IOException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        } catch (JAXBException e) {
            LOG.error("url: "+metricUrl,e);
            throw new RuntimeException(e);
        }
    }

}
