package net.awired.visuwall.core.business.process.capabilities;

import java.util.Map.Entry;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.MetricCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import org.springframework.stereotype.Component;

@Component
public class MetricCapabilityProcess {
    @Transient
    String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

    void enhanceWithQualityAnalysis(Project analyzedProject, BasicCapability plugin, String... metrics) {
        ProjectId projectId = analyzedProject.getProjectId();
        Build build = analyzedProject.getCompletedBuild();

        TestResult unitTestResultToMerge = null;
        TestResult integrationTestResultToMerge = null;
        if (build != null) {
            unitTestResultToMerge = build.getUnitTestResult();
            integrationTestResultToMerge = build.getIntegrationTestResult();
        }

        QualityResult qualityResultToMerge = analyzedProject.getQualityResult();
        if (plugin instanceof MetricCapability) {
            addQualityAnalysis((MetricCapability) plugin, projectId, qualityResultToMerge, metrics);
        }
        if (plugin instanceof TestCapability) {
            addUnitTestsAnalysis((TestCapability) plugin, projectId, unitTestResultToMerge);
            addIntegrationTestsAnalysis((TestCapability) plugin, projectId, integrationTestResultToMerge);
        }
    }

    void addQualityAnalysis(MetricCapability metricPlugin, ProjectId projectId, QualityResult qualityResultToMerge,
            String... metrics) {
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

    void addIntegrationTestsAnalysis(TestCapability testsPlugin, ProjectId projectId,
            TestResult integrationTestResultToMerge) {
        TestResult integrationTestsAnalysis = testsPlugin.analyzeIntegrationTests(projectId);
        if (integrationTestsAnalysis != null && integrationTestResultToMerge != null) {
            mergeTestAnalysis(integrationTestResultToMerge, integrationTestsAnalysis);
        }
    }

    void addUnitTestsAnalysis(TestCapability testsPlugin, ProjectId projectId, TestResult unitTestResultToMerge) {
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
