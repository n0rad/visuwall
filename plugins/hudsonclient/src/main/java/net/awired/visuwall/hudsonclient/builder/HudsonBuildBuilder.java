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
import java.util.Set;

import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.helper.HudsonXmlHelper;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.UniformInterfaceException;

public class HudsonBuildBuilder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonBuildBuilder.class);

    @VisibleForTesting
    TestResultBuilder testResultBuilder;

    public HudsonBuildBuilder() {
        this.testResultBuilder = new TestResultBuilder();
    }

    public HudsonBuild createHudsonBuild(HudsonMavenMavenModuleSetBuild setBuild,
            HudsonMavenReportersSurefireAggregatedReport surefireReport, Set<HudsonCommiter> commiters) {
        checkSetBuild(setBuild);
        HudsonBuild hudsonBuild = createHudsonBuild(setBuild, commiters);
        addTestResults(setBuild, surefireReport, hudsonBuild);
        return hudsonBuild;
    }

    public HudsonBuild createHudsonBuild(HudsonMavenMavenModuleSetBuild setBuild, Set<HudsonCommiter> commiters) {
        checkSetBuild(setBuild);
        HudsonBuild hudsonBuild = new HudsonBuild();
        hudsonBuild.setState(HudsonXmlHelper.getState(setBuild));
        hudsonBuild.setDuration(setBuild.getDuration());
        hudsonBuild.setStartTime(new Date(setBuild.getTimestamp()));
        hudsonBuild.setSuccessful(HudsonXmlHelper.isSuccessful(setBuild));
        hudsonBuild.setCommiters(commiters);
        hudsonBuild.setBuildNumber(setBuild.getNumber());
        return hudsonBuild;
    }

    private void addTestResults(HudsonMavenMavenModuleSetBuild setBuild,
            HudsonMavenReportersSurefireAggregatedReport surefireReport, HudsonBuild hudsonBuild) {
        try {
            HudsonTestResult unitTestResult = testResultBuilder.buildUnitTestResult(surefireReport);
            HudsonTestResult integrationTestResult = testResultBuilder.buildIntegrationTestResult(surefireReport);
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

    private void checkSetBuild(HudsonMavenMavenModuleSetBuild setBuild) {
        Preconditions.checkNotNull(setBuild, "setBuild is mandatory");
    }

}
