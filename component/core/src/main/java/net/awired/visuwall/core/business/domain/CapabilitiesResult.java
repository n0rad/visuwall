package net.awired.visuwall.core.business.domain;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityResult;

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
