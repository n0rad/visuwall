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

import static org.junit.Assert.assertEquals;

import java.net.URL;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.util.ClasspathFiles;

import org.junit.Ignore;
import org.junit.Test;

public class TestResultBuilderTest {

    @Ignore
    @Test
    public void should_create_valid_unit_test_result() throws Exception {
        int failCount = 1;
        int passCount = 3;
        int skipCount = 5;
        int totalCount = 9;

        HudsonMavenReportersSurefireAggregatedReport surefireReport = createModuleSetFrom("surefire_aggregated_report.xml");

        TestResultBuilder testResultBuilder = new TestResultBuilder();
        TestResult testResult = testResultBuilder.buildUnitTestResult(surefireReport);

        assertEquals(failCount, testResult.getFailCount());
        assertEquals(passCount, testResult.getPassCount());
        assertEquals(skipCount, testResult.getSkipCount());
        assertEquals(totalCount, testResult.getTotalCount());
    }

    private HudsonMavenReportersSurefireAggregatedReport createModuleSetFrom(String fileName) throws Exception {
        String file = ClasspathFiles.getAbsolutePathFile("hudson/" + fileName);
        URL url = new URL(file);
        JAXBContext newInstance = JAXBContext.newInstance(HudsonMavenReportersSurefireAggregatedReport.class);
        Unmarshaller unmarshaller = newInstance.createUnmarshaller();
        JAXBElement<HudsonMavenReportersSurefireAggregatedReport> element = (JAXBElement<HudsonMavenReportersSurefireAggregatedReport>) unmarshaller
                .unmarshal(url);
        return element.getValue();
    }
}
