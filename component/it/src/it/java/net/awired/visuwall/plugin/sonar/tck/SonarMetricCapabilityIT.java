/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.plugin.sonar.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.tck.MetricCapabilityTCK;
import net.awired.visuwall.plugin.sonar.SonarConnection;
import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class SonarMetricCapabilityIT implements MetricCapabilityTCK {

    private static MetricCapability sonar = new SonarConnection();

    @BeforeClass
    public static void init() throws ConnectionException {
        sonar.connect(Urls.AWIRED_SONAR, null, null);
    }

    @Test
    public void should_analyze_quality_with_all_metrics() {
        SoftwareProjectId projectId = struts();
        QualityResult quality = sonar.analyzeQuality(projectId);
        Set<Entry<String, QualityMeasure>> measures = quality.getMeasures();
        assertFalse(measures.isEmpty());
        for (Entry<String, QualityMeasure> measure : measures) {
            assertNotNull(measure.getValue().getValue());
        }
    }

    @Ignore
    @Test
    public void should_count_exact_it_and_ut() {
        SoftwareProjectId projectId = librestry();

        SonarConnection sonarPlugin = new SonarConnection();
        sonarPlugin.connect(Urls.FLUXX_SONAR);
        TestResult unitTestsAnalysis = sonarPlugin.analyzeUnitTests(projectId);
        assertEquals(18.4, unitTestsAnalysis.getCoverage(), 0);
        assertEquals(1, unitTestsAnalysis.getFailCount());
        assertEquals(5, unitTestsAnalysis.getSkipCount());
        assertEquals(3, unitTestsAnalysis.getPassCount());
        assertEquals(9, unitTestsAnalysis.getTotalCount());

        TestResult integrationTestsAnalysis = sonarPlugin.analyzeIntegrationTests(projectId);
        assertEquals(47.4, integrationTestsAnalysis.getCoverage(), 0);
        // Sonar/Jacoco don't count IT tests
        assertEquals(0, integrationTestsAnalysis.getFailCount());
        assertEquals(0, integrationTestsAnalysis.getSkipCount());
        assertEquals(0, integrationTestsAnalysis.getPassCount());
        assertEquals(0, integrationTestsAnalysis.getTotalCount());
    }

    @Override
    @Test
    public void should_analyze_quality_with_only_3_metrics() {
        String[] keys = { "lines", "coverage", "lcom4" };
        QualityResult analysis = sonar.analyzeQuality(librestry(), keys);
        for (String key : keys) {
            assertNotNull(analysis.getMeasure(key));
        }
    }

    @Override
    @Test
    public void should_get_metrics_by_category() {
        Map<String, List<QualityMetric>> metricsByCategory = sonar.getMetricsByCategory();
        assertFalse(metricsByCategory.isEmpty());
    }

    private SoftwareProjectId librestry() {
        return new SoftwareProjectId("fr.xebia.librestry:librestry");
    }

    private SoftwareProjectId struts() {
        return new SoftwareProjectId("org.apache.struts:struts-core");
    }

}
