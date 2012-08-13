package net.awired.visuwall.core.business.domain.capability;

import net.awired.visuwall.api.domain.TestResult;

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
