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
package fr.norad.visuwall;

import org.springframework.stereotype.Component;
import fr.norad.visuwall.domain.Build;
import fr.norad.visuwall.domain.Project;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.plugin.capability.BasicCapability;

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

        //        TestResult unitTestResultToMerge = null;
        //        TestResult integrationTestResultToMerge = null;
        //        if (build != null) {
        //            unitTestResultToMerge = build.getUnitTestResult();
        //            integrationTestResultToMerge = build.getIntegrationTestResult();
        //        }

        //        if (plugin instanceof TestCapability) {
        //            addUnitTestsAnalysis((TestCapability) plugin, projectId, unitTestResultToMerge);
        //            addIntegrationTestsAnalysis((TestCapability) plugin, projectId, integrationTestResultToMerge);
        //        }
    }

}
