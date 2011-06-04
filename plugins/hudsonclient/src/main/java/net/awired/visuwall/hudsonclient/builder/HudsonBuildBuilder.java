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

package net.awired.visuwall.hudsonclient.builder;

import java.util.Date;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.helper.HudsonXmlHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

public class HudsonBuildBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonBuildBuilder.class);

    private TestResultBuilder testResultBuilder;

    public HudsonBuildBuilder() {
        this.testResultBuilder = new TestResultBuilder();
    }

    public HudsonBuildBuilder(TestResultBuilder testResultBuilder) {
        this.testResultBuilder = testResultBuilder;
    }

    public HudsonBuild createHudsonBuild(HudsonMavenMavenModuleSetBuild setBuild,
            HudsonMavenReportersSurefireAggregatedReport surefireReport) {
        Preconditions.checkNotNull(setBuild, "setBuild is mandatory");
        HudsonBuild hudsonBuild = new HudsonBuild();
        hudsonBuild.setState(HudsonXmlHelper.getState(setBuild));
        hudsonBuild.setDuration(setBuild.getDuration());
        hudsonBuild.setStartTime(new Date(setBuild.getTimestamp()));
        hudsonBuild.setSuccessful(HudsonXmlHelper.isSuccessful(setBuild));
        hudsonBuild.setCommiters(HudsonXmlHelper.getCommiters(setBuild));
        hudsonBuild.setBuildNumber(setBuild.getNumber());
        addTestResults(setBuild, surefireReport, hudsonBuild);
        return hudsonBuild;
    }

    private void addTestResults(HudsonMavenMavenModuleSetBuild setBuild,
            HudsonMavenReportersSurefireAggregatedReport surefireReport, HudsonBuild hudsonBuild) {
        try {
            TestResult unitTestResult = testResultBuilder.buildUnitTestResult(surefireReport);
            TestResult integrationTestResult = testResultBuilder.buildIntegrationTestResult(surefireReport);
            hudsonBuild.setUnitTestResult(unitTestResult);
            hudsonBuild.setIntegrationTestResult(integrationTestResult);
        } catch (UniformInterfaceException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no test result for " + setBuild.getFullDisplayName());
            }
        } catch (ClientHandlerException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("no test result " + setBuild.getFullDisplayName());
            }
        }
    }

}
