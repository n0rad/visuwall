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

package net.awired.visuwall.core.business.process.capabilities;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.Build;
import net.awired.visuwall.core.business.domain.Project;
import org.springframework.stereotype.Component;

@Component
public class TestCapabilityProcess {

    private void enhanceWithTests(Project analyzedProject, BasicCapability plugin) {
        //        ProjectId projectId = analyzedProject.getProjectId();
        Build build = analyzedProject.getLastBuild();

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
