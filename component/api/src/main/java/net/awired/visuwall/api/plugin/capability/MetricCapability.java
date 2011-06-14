package net.awired.visuwall.api.plugin.capability;

import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.Connection;

public interface MetricCapability extends Connection {

    /**
     * @return Quality Metrics sorted by category
     */
    Map<String, List<QualityMetric>> getMetricsByCategory();

    /**
     * Generate a complete quality reporting for a project defined by <code>projectId</code>
     * 
     * @param projectId
     * @param metrics
     *            You can specify the metrics you only want to analyze.
     * @return
     */
    QualityResult analyzeQuality(ProjectId projectId, String... metrics);

}
