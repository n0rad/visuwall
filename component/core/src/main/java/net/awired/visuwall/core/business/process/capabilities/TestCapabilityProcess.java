package net.awired.visuwall.core.business.process.capabilities;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.Build;
import net.awired.visuwall.core.business.domain.Project;
import org.springframework.stereotype.Component;

@Component
public class TestCapabilityProcess {

    private void enhanceWithTests(Project analyzedProject, BasicCapability plugin) {
        //        ProjectId projectId = analyzedProject.getProjectId();
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
            unitTestResultToMerge = build.getUnitTestResult();
            integrationTestResultToMerge = build.getIntegrationTestResult();
        }

        //        if (plugin instanceof TestCapability) {
        //            addUnitTestsAnalysis((TestCapability) plugin, projectId, unitTestResultToMerge);
        //            addIntegrationTestsAnalysis((TestCapability) plugin, projectId, integrationTestResultToMerge);
        //        }
    }

}
