package net.awired.visuwall.core.business.process.capabilities;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import org.springframework.stereotype.Component;

@Component
public class TestCapabilityProcess {

    private void enhanceWithTests(Project analyzedProject, BasicCapability plugin) {
        ProjectId projectId = analyzedProject.getProjectId();
        Build build = analyzedProject.getCompletedBuild();

        TestResult unitTestResultToMerge = null;
        TestResult integrationTestResultToMerge = null;
        if (build != null) {
            unitTestResultToMerge = build.getUnitTestResult();
            integrationTestResultToMerge = build.getIntegrationTestResult();
        }

        //        if (plugin instanceof TestCapability) {
        //            addUnitTestsAnalysis((TestCapability) plugin, projectId, unitTestResultToMerge);
        //            addIntegrationTestsAnalysis((TestCapability) plugin, projectId, integrationTestResultToMerge);
        //        }
    }

}
