/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.core.business.process.capabilities;

import java.util.Map.Entry;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.domain.TestResult;
import fr.norad.visuwall.api.domain.quality.QualityMeasure;
import fr.norad.visuwall.api.domain.quality.QualityResult;
import fr.norad.visuwall.api.exception.BuildNotFoundException;
import fr.norad.visuwall.api.plugin.capability.BasicCapability;
import fr.norad.visuwall.api.plugin.capability.MetricCapability;
import fr.norad.visuwall.api.plugin.capability.TestCapability;
import fr.norad.visuwall.core.business.domain.Build;
import fr.norad.visuwall.core.business.domain.Project;
import org.springframework.stereotype.Component;

@Component
public class MetricCapabilityProcess {
    public static String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

    public void updateLastBuildMetrics(Project project) {
        for (SoftwareProjectId softwareProjectId : project.getCapabilities().keySet()) {
            BasicCapability capability = project.getCapabilities().get(softwareProjectId);

        }
    }

    void enhanceWithQualityAnalysis(Project analyzedProject, BasicCapability plugin, String... metrics) {
        SoftwareProjectId projectId = null;// analyzedProject.getProjectId();
        Build build = null;
        try {
            build = analyzedProject.getLastBuild();
        } catch (BuildNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        TestResult unitTestResultToMerge = null;
        TestResult integrationTestResultToMerge = null;
        if (build != null) {
            //            unitTestResultToMerge = build.getUnitTestResult();
            //            integrationTestResultToMerge = build.getIntegrationTestResult();
        }

        //TODO remove all function
        //        QualityResult qualityResultToMerge = analyzedProject.getQualityResult();
        QualityResult qualityResultToMerge = null;
        if (plugin instanceof MetricCapability) {
            addQualityAnalysis((MetricCapability) plugin, projectId, qualityResultToMerge, metrics);
        }
        if (plugin instanceof TestCapability) {
            addUnitTestsAnalysis((TestCapability) plugin, projectId, unitTestResultToMerge);
            addIntegrationTestsAnalysis((TestCapability) plugin, projectId, integrationTestResultToMerge);
        }
    }

    void addQualityAnalysis(MetricCapability metricPlugin, SoftwareProjectId projectId,
            QualityResult qualityResultToMerge, String... metrics) {
        QualityResult qualityAnalysis = metricPlugin.analyzeQuality(projectId, metrics);
        if (qualityAnalysis != null) {
            mergeQualityAnalysis(qualityResultToMerge, qualityAnalysis);
        }
    }

    void mergeQualityAnalysis(QualityResult qualityResultToMerge, QualityResult qualityAnalysis) {
        for (Entry<String, QualityMeasure> entry : qualityAnalysis.getMeasures()) {
            qualityResultToMerge.add(entry.getKey(), entry.getValue());
        }
    }

    void addIntegrationTestsAnalysis(TestCapability testsPlugin, SoftwareProjectId projectId,
            TestResult integrationTestResultToMerge) {
        TestResult integrationTestsAnalysis = testsPlugin.analyzeIntegrationTests(projectId);
        if (integrationTestsAnalysis != null && integrationTestResultToMerge != null) {
            mergeTestAnalysis(integrationTestResultToMerge, integrationTestsAnalysis);
        }
    }

    void addUnitTestsAnalysis(TestCapability testsPlugin, SoftwareProjectId projectId,
            TestResult unitTestResultToMerge) {
        TestResult unitTestsAnalysis = testsPlugin.analyzeUnitTests(projectId);
        if (unitTestsAnalysis != null && unitTestResultToMerge != null) {
            mergeTestAnalysis(unitTestResultToMerge, unitTestsAnalysis);
        }
    }

    void mergeTestAnalysis(TestResult testResultToMerge, TestResult testsAnalysis) {
        if (testsAnalysis.getCoverage() > 0) {
            testResultToMerge.setCoverage(testsAnalysis.getCoverage());
        }
        if (testsAnalysis.getFailCount() > 0) {
            testResultToMerge.setFailCount(testsAnalysis.getFailCount());
        }
        if (testsAnalysis.getPassCount() > 0) {
            testResultToMerge.setPassCount(testsAnalysis.getPassCount());
        }
        if (testsAnalysis.getSkipCount() > 0) {
            testResultToMerge.setSkipCount(testsAnalysis.getSkipCount());
        }
    }

}
