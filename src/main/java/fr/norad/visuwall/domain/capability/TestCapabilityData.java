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
package fr.norad.visuwall.domain.capability;


import fr.norad.visuwall.domain.TestResult;

public class TestCapabilityData implements CapabilityData {

    private TestResult unitTestResult;
    private TestResult integrationTestResult;

    public TestResult getUnitTestResult() {
        return unitTestResult;
    }

    public void setUnitTestResult(TestResult unitTestResult) {
        this.unitTestResult = unitTestResult;
    }

    public TestResult getIntegrationTestResult() {
        return integrationTestResult;
    }

    public void setIntegrationTestResult(TestResult integrationTestResult) {
        this.integrationTestResult = integrationTestResult;
    }

}
