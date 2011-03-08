package com.jsmadja.wall.hudsonclient.builder;

import java.util.ArrayList;
import java.util.List;

import org.apache.xerces.dom.ElementNSImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.NodeList;

import com.jsmadja.wall.hudsonclient.domain.TestResult;
import com.jsmadja.wall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import com.jsmadja.wall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonTasksTestAggregatedTestResultActionChildReport;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * TestResultBuilder is used to manipulate Hudson Test data
 */
public final class TestResultBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultBuilder.class);

    /**
     * @param testResultResource
     * @return TestResult data if available
     */
    public TestResult build(WebResource testResultResource) {
        TestResult testResult = new TestResult();
        try {
            HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource.get(HudsonMavenReportersSurefireAggregatedReport.class);
            testResult.setFailCount(surefireReport.getFailCount());
            testResult.setPassCount(computePassCount(surefireReport));
            testResult.setSkipCount(surefireReport.getSkipCount());
            testResult.setTotalCount(surefireReport.getTotalCount());
            int integrationTestCount = countIntegrationTestsIn(surefireReport);
            testResult.setIntegrationTestCount(integrationTestCount);
        } catch(UniformInterfaceException e) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("no test result for "+testResultResource.getURI().toString());
            }
        } catch(ClientHandlerException e) {
            if(LOG.isDebugEnabled()) {
                LOG.debug("no test result "+testResultResource.getURI().toString());
            }
        }
        return testResult;
    }

    private int computePassCount(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
        return surefireReport.getTotalCount() - surefireReport.getFailCount() - surefireReport.getSkipCount();
    }

    private int countIntegrationTestsIn(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
        int integrationTestCount = 0;
        for (HudsonTasksTestAggregatedTestResultActionChildReport childReport:surefireReport.getChildReport()) {
            ElementNSImpl childReportResult = (ElementNSImpl) childReport.getResult();
            List<String> testNames = findTestNamesFrom(childReportResult);
            for (String testName:testNames) {
                if (isIntegrationTest(testName)) {
                    integrationTestCount++;
                }
            }
        }
        return integrationTestCount;
    }

    private List<String> findTestNamesFrom(ElementNSImpl result) {
        List<String> testNames = new ArrayList<String>();

        NodeList cases = result.getElementsByTagName("className");
        for (int i=0; i < cases.getLength(); i++) {
            String testName = cases.item(i).getFirstChild().getNodeValue();
            testNames.add(testName);
        }

        return testNames;
    }

    private boolean isIntegrationTest(String testName) {
        return testName.endsWith("ITTest") || testName.contains(".it.");
    }
}
