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

package net.awired.clients.hudson;

import java.util.ArrayList;
import java.util.List;
import net.awired.clients.hudson.domain.HudsonTest;
import net.awired.clients.hudson.domain.HudsonTestResult;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonTasksTestAggregatedTestResultActionChildReport;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

/**
 * TestResultBuilder is used to manipulate Hudson Test data
 */
public class TestResultBuilder {

    public HudsonTestResult buildUnitTestResult(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
        HudsonTestResult unitTestResult = new HudsonTestResult();
        List<HudsonTasksTestAggregatedTestResultActionChildReport> tests = surefireReport.getChildReport();
        countUnitTests(unitTestResult, tests);
        return unitTestResult;
    }

    public HudsonTestResult buildIntegrationTestResult(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
        HudsonTestResult integrationTestResult = new HudsonTestResult();
        List<HudsonTasksTestAggregatedTestResultActionChildReport> tests = surefireReport.getChildReport();
        countIntegrationTests(integrationTestResult, tests);
        return integrationTestResult;
    }

    private void countUnitTests(HudsonTestResult unitTestsResult,
            List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
        List<HudsonTest> tests = createTestsFrom(testReport);
        for (HudsonTest test : tests) {
            if (test.isUnitTest()) {
                updateTestResult(unitTestsResult, test);
            }
        }
    }

    private void countIntegrationTests(HudsonTestResult integrationTestsResult,
            List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
        List<HudsonTest> tests = createTestsFrom(testReport);
        for (HudsonTest test : tests) {
            if (test.isIntegrationTest()) {
                updateTestResult(integrationTestsResult, test);
            }
        }
    }

    private void updateTestResult(HudsonTestResult unitTestsResult, HudsonTest test) {
        String status = test.getStatus();
        if ("FAILED".equals(status)) {
            unitTestsResult.setFailCount(unitTestsResult.getFailCount() + 1);
        }
        if ("SKIPPED".equals(status)) {
            unitTestsResult.setSkipCount(unitTestsResult.getSkipCount() + 1);
        }
        if ("PASSED".equals(status)) {
            unitTestsResult.setPassCount(unitTestsResult.getPassCount() + 1);
        }
    }

    private List<HudsonTest> createTestsFrom(List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
        List<HudsonTest> tests = new ArrayList<HudsonTest>();
        for (HudsonTasksTestAggregatedTestResultActionChildReport childReport : testReport) {
            Element childReportResult = (Element) childReport.getResult();
            NodeList cases = childReportResult.getElementsByTagName("case");
            for (int i = 0; i < cases.getLength(); i++) {
                Node testNode = cases.item(i);
                HudsonTest test = createTestFrom(testNode);
                tests.add(test);
            }
        }
        return tests;
    }

    private HudsonTest createTestFrom(Node testNode) {
        HudsonTest test = new HudsonTest();
        NodeList testAttributes = testNode.getChildNodes();
        for (int j = 0; j < testAttributes.getLength(); j++) {
            Node attributeNode = testAttributes.item(j);
            String attributeName = attributeNode.getNodeName();
            String attributeValue = attributeNode.getTextContent();
            if ("className".equals(attributeName)) {
                test.setClassName(attributeValue);
            } else {
                if ("status".equals(attributeName)) {
                    test.setStatus(attributeValue);
                }
            }
        }
        return test;
    }

}
