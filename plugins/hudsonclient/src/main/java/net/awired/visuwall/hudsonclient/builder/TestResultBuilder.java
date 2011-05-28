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
import org.w3c.dom.NodeList;

import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

/**
 * TestResultBuilder is used to manipulate Hudson Test data
 */
public final class TestResultBuilder {

	private static final Logger LOG = LoggerFactory.getLogger(TestResultBuilder.class);

	public TestResult buildUnitTestResult(WebResource testResultResource) {
		TestResult unitTestResult = new TestResult();
		try {
			HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource
			        .get(HudsonMavenReportersSurefireAggregatedReport.class);
			unitTestResult.setFailCount(surefireReport.getFailCount());
			unitTestResult.setPassCount(computePassCount(surefireReport));
			unitTestResult.setSkipCount(surefireReport.getSkipCount());
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
			int integrationTestCount = countIntegrationTestsIn(surefireReport);
			integrationTestResult.setPassCount(integrationTestCount);
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

	private int computePassCount(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
		return surefireReport.getTotalCount() - surefireReport.getFailCount() - surefireReport.getSkipCount();
	}

	private int countIntegrationTestsIn(HudsonMavenReportersSurefireAggregatedReport surefireReport) {
		int integrationTestCount = 0;
		for (HudsonTasksTestAggregatedTestResultActionChildReport childReport : surefireReport.getChildReport()) {
			Element childReportResult = (Element) childReport.getResult();
			List<String> testNames = findTestNamesFrom(childReportResult);
			for (String testName : testNames) {
				if (isIntegrationTest(testName)) {
					integrationTestCount++;
				}
			}
		}
		return integrationTestCount;
	}

	private List<String> findTestNamesFrom(Element result) {
		List<String> testNames = new ArrayList<String>();

		NodeList cases = result.getElementsByTagName("className");
		for (int i = 0; i < cases.getLength(); i++) {
			String testName = cases.item(i).getFirstChild().getNodeValue();
			testNames.add(testName);
		}

		return testNames;
	}

	private boolean isIntegrationTest(String testName) {
		return testName.endsWith("ITTest") || testName.contains(".it.") || testName.endsWith("IT");
	}
}
