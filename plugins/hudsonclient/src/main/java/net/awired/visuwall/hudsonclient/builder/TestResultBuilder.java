/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.hudsonclient.builder;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonTasksTestAggregatedTestResultActionChildReport;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * TestResultBuilder is used to manipulate Hudson Test data
 */
public final class TestResultBuilder {

	class Test {
		public String className;
		public String status;
		@Override
		public String toString() {
			return className+":"+status;
		}
	}
	
	private static final Logger LOG = LoggerFactory.getLogger(TestResultBuilder.class);

	public TestResult buildUnitTestResult(WebResource testResultResource) {
		TestResult unitTestResult = new TestResult();
		try {
			HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource
			        .get(HudsonMavenReportersSurefireAggregatedReport.class);
			List<HudsonTasksTestAggregatedTestResultActionChildReport> tests = surefireReport.getChildReport();
			countUnitTests(unitTestResult, tests);
		} catch (UniformInterfaceException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result for " + testResultResource.getURI().toString());
			}
		} catch (ClientHandlerException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result " + testResultResource.getURI().toString());
			}
		}
		return unitTestResult;
	}
	

	public TestResult buildIntegrationTestResult(WebResource testResultResource) {
		TestResult integrationTestResult = new TestResult();
		try {
			HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource
			        .get(HudsonMavenReportersSurefireAggregatedReport.class);
			List<HudsonTasksTestAggregatedTestResultActionChildReport> tests = surefireReport.getChildReport();
			countIntegrationTests(integrationTestResult, tests);
		} catch (UniformInterfaceException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result for " + testResultResource.getURI().toString());
			}
		} catch (ClientHandlerException e) {
			if (LOG.isDebugEnabled()) {
				LOG.debug("no test result " + testResultResource.getURI().toString());
			}
		}
		return integrationTestResult;
	}

	private void countUnitTests(TestResult unitTestsResult,
			List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
		List<Test> tests = createTestsFrom(testReport);
		for (Test test:tests) {
			if (isUnitTest(test)) {
				updateTestResult(unitTestsResult, test);
			}
		}
	}

	private void countIntegrationTests(TestResult integrationTestsResult,
			List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
		List<Test> tests = createTestsFrom(testReport);
		for (Test test:tests) {
			if (isIntegrationTest(test)) {
				updateTestResult(integrationTestsResult, test);
			}
		}
	}
	
	private void updateTestResult(TestResult unitTestsResult, Test test) {
		String status = test.status;
		if ("FAILED".equals(status))
			unitTestsResult.setFailCount(unitTestsResult.getFailCount()  + 1);
		if ("SKIPPED".equals(status))
			unitTestsResult.setSkipCount(unitTestsResult.getSkipCount()  + 1);
		if ("PASSED".equals(status))
			unitTestsResult.setPassCount(unitTestsResult.getPassCount()  + 1);
	}

	private boolean isUnitTest(Test test) {
		return !isIntegrationTest(test);
	}

	private List<Test> createTestsFrom(
			List<HudsonTasksTestAggregatedTestResultActionChildReport> testReport) {
		List<Test> tests = new ArrayList<Test>();
		for (HudsonTasksTestAggregatedTestResultActionChildReport childReport : testReport) {
			Element childReportResult = (Element) childReport.getResult();
			NodeList cases = childReportResult.getElementsByTagName("case");
			for (int i = 0; i < cases.getLength(); i++) {
				Node testNode = cases.item(i);
				Test test = createTestFrom(testNode);
				tests.add(test);
			}
		}
		return tests;
	}

	private Test createTestFrom(Node testNode) {
		Test test = new Test();
		NodeList testAttributes = testNode.getChildNodes();
		for (int j = 0; j < testAttributes.getLength() ; j++) {
			Node attributeNode = testAttributes.item(j);
			String attributeName = attributeNode.getNodeName();
			String attributeValue = attributeNode.getTextContent();
			if ("className".equals(attributeName))
				test.className = attributeValue;
			else if ("status".equals(attributeName))
				test.status = attributeValue; 
		}
		return test;
	}

	private boolean isIntegrationTest(Test test) {
		String testName = test.className;
		return testName.endsWith("ITTest") || testName.contains(".it.") || testName.endsWith("IT");
	}
}
