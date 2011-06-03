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

import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonModelUser;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;

import org.junit.Assert;
import org.junit.Test;

public class HudsonBuildBuilderTest {

    @Test
    public void should_create_valid_hudson_build() {
        long duration = 123L;
        int buildNumber = 34;
        String[] commiters = new String[] { "dude", "sweet" };
        TestResult integrationTests = new TestResult();
        TestResult unitTests = new TestResult();
        Date startTime = new Date();
        String state = "NEW";

        TestResultBuilder testResultBuilder = mock(TestResultBuilder.class);
        when(testResultBuilder.buildIntegrationTestResult(any(HudsonMavenReportersSurefireAggregatedReport.class)))
                .thenReturn(integrationTests);
        when(testResultBuilder.buildUnitTestResult(any(HudsonMavenReportersSurefireAggregatedReport.class)))
                .thenReturn(unitTests);

        List<HudsonModelUser> users = new ArrayList<HudsonModelUser>();
        users.add(new HudsonModelUser());
        users.add(new HudsonModelUser());
        users.get(0).setFullName("dude");
        users.get(1).setFullName("sweet");

        HudsonMavenMavenModuleSetBuild setBuild = mock(HudsonMavenMavenModuleSetBuild.class);
        when(setBuild.getDuration()).thenReturn(duration);
        when(setBuild.getCulprit()).thenReturn(users);
        when(setBuild.getNumber()).thenReturn(buildNumber);
        when(setBuild.getTimestamp()).thenReturn(startTime.getTime());

        HudsonMavenReportersSurefireAggregatedReport surefireReport = mock(HudsonMavenReportersSurefireAggregatedReport.class);

        HudsonBuildBuilder hudsonBuildBuilder = new HudsonBuildBuilder(testResultBuilder);
        HudsonBuild hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, surefireReport);

        Assert.assertEquals(duration, hudsonBuild.getDuration());
        Assert.assertEquals(buildNumber, hudsonBuild.getBuildNumber());
        Assert.assertArrayEquals(commiters, hudsonBuild.getCommiters());
        Assert.assertEquals(startTime, hudsonBuild.getStartTime());
        Assert.assertEquals(state, hudsonBuild.getState());
        Assert.assertEquals(integrationTests, hudsonBuild.getIntegrationTestResult());
        Assert.assertEquals(unitTests, hudsonBuild.getUnitTestResult());
    }
}
