package net.awired.visuwall.plugin.sonar;

import static org.apache.commons.lang.StringUtils.isBlank;
import static org.apache.commons.lang.StringUtils.isNotBlank;

import java.util.Map;

import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.plugin.sonar.exception.SonarMetricNotFoundException;

import org.sonar.wsclient.Sonar;
import org.sonar.wsclient.connectors.ConnectionException;
import org.sonar.wsclient.services.Measure;
import org.sonar.wsclient.services.Resource;
import org.sonar.wsclient.services.ResourceQuery;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class MeasureFinder {

    private MetricsLoader metricsLoader = new MetricsLoader();

    private String sonarUrl;
    private String login;
    private String password;

    /**
     * http://docs.codehaus.org/display/SONAR/Web+Service+API
     */
    private Sonar sonar;

    private Map<String, QualityMetric> metrics;

    public MeasureFinder(String sonarUrl) {
        this.sonarUrl = sonarUrl;
    }

    public MeasureFinder(String sonarUrl, String login, String password) {
        this(sonarUrl);
        this.login = login;
        this.password = password;
    }

    public void init() {
        if (isBlank(sonarUrl)) {
            throw new IllegalStateException("sonarUrl can't be null.");
        }
        if (isNotBlank(login) && isNotBlank(password)) {
            sonar = Sonar.create(sonarUrl, login, password);
        } else {
            sonar = Sonar.create(sonarUrl);
        }
        metrics = metricsLoader.createMetricList(sonarUrl);
    }

    public QualityMeasure createQualityMeasure(String projectId, String key) throws SonarMetricNotFoundException {
        Measure measure = findMeasure(projectId, key);
        if (measure != null) {
            Double value = measure.getValue();
            if (value != null) {
                QualityMeasure qualityMeasure = new QualityMeasure();
                qualityMeasure.setName(metrics.get(key).getName());
                qualityMeasure.setValue(value);
                qualityMeasure.setFormattedValue(measure.getFormattedValue());
                return qualityMeasure;
            }
        }
        throw new SonarMetricNotFoundException("metric '"+key+"' is not found for project '"+projectId+"'");
    }

    public Measure findMeasure(String projectId, String measureKey) throws SonarMetricNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId");
        try {
            ResourceQuery query = ResourceQuery.createForMetrics(projectId, measureKey);
            Resource resource = sonar.find(query);
            if (resource == null) {
                throw new SonarMetricNotFoundException("Metric "+measureKey+" not found for project "+projectId+" in Sonar "+sonarUrl);
            }
            return resource.getMeasure(measureKey);
        } catch(ConnectionException e) {
            throw new SonarMetricNotFoundException("Metric "+measureKey+" not found for project "+projectId+" in Sonar "+sonarUrl, e);
        }
    }

    public String[] getAllMetricKeys() {
        return metrics.keySet().toArray(new String[]{});
    }

    @VisibleForTesting
    void setMetricsLoader(MetricsLoader metricsLoader) {
        this.metricsLoader = metricsLoader;
    }

    @VisibleForTesting
    void setSonar(Sonar sonar) {
        this.sonar = sonar;
    }

    @VisibleForTesting
    void setMetrics(Map<String, QualityMetric> metrics) {
        this.metrics = metrics;
    }

}
