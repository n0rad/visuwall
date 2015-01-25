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
package fr.norad.visuwall.domain;


import fr.norad.visuwall.domain.quality.QualityResult;

public class CapabilitiesResult {

    // TODO change to a generic way
    private TestResult unitTestResult;
    private TestResult integrationTestResult;
    private QualityResult qualityResult;

    public void setIntegrationTestResult(TestResult integrationTestResult) {
        if (this.integrationTestResult == null) {
            this.integrationTestResult = new TestResult();
        }
        setTestResult(integrationTestResult, this.integrationTestResult);
    }

    public void setUnitTestResult(TestResult unitTestResult) {
        if (this.unitTestResult == null) {
            this.unitTestResult = new TestResult();
        }
        setTestResult(unitTestResult, this.unitTestResult);
    }

    private void setTestResult(TestResult from, TestResult to) {
        to.setCoverage(from.getCoverage());
        to.setFailCount(from.getFailCount());
        to.setPassCount(from.getPassCount());
        to.setSkipCount(from.getSkipCount());
    }

    public void setQualityResult(QualityResult qualityResult) {
        this.qualityResult = qualityResult;
    }

    public QualityResult getQualityResult() {
        return qualityResult;
    }

    public TestResult getUnitTestResult() {
        return unitTestResult;
    }

    public TestResult getIntegrationTestResult() {
        return integrationTestResult;
    }

}
