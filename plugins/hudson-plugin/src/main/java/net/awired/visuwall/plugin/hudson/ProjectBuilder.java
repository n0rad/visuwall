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

package net.awired.visuwall.plugin.hudson;

import java.util.HashSet;
import java.util.Set;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;
import com.google.common.base.Preconditions;

public class ProjectBuilder {

    public Project buildProjectFrom(HudsonProject hudsonProject) {
        Preconditions.checkNotNull(hudsonProject, "hudsonProject is mandatory");

        Project project = new Project(hudsonProject.getName());
        project.setBuildNumbers(hudsonProject.getBuildNumbers());

        addCurrentAndCompletedBuilds(project, hudsonProject);

        return project;
    }

    public void addCurrentAndCompletedBuilds(Project project, HudsonProject hudsonProject) {
        HudsonBuild completedBuild = hudsonProject.getCompletedBuild();
        if (completedBuild != null) {
            project.setCompletedBuild(buildBuildFrom(completedBuild));
        }
        HudsonBuild currentBuild = hudsonProject.getCurrentBuild();
        if (currentBuild != null) {
            project.setCurrentBuild(buildBuildFrom(currentBuild));
        }
    }

    public Build buildBuildFrom(HudsonBuild hudsonBuild) {
        Preconditions.checkNotNull(hudsonBuild, "hudsonBuild");
        Build build = new Build();

        HudsonTestResult hudsonUnitTestResult = hudsonBuild.getUnitTestResult();
        HudsonTestResult hudsonIntegrationTestResult = hudsonBuild.getIntegrationTestResult();

        if (hudsonUnitTestResult != null) {
            TestResult unitTestResult = createTestResult(hudsonUnitTestResult);
            build.setUnitTestResult(unitTestResult);
        }
        if (hudsonIntegrationTestResult != null) {
            TestResult integrationTestResult = createTestResult(hudsonUnitTestResult);
            build.setIntegrationTestResult(integrationTestResult);
        }

        Set<Commiter> commiters = createCommiters(hudsonBuild.getCommiters());
        build.setCommiters(commiters);
        build.setDuration(hudsonBuild.getDuration());
        build.setStartTime(hudsonBuild.getStartTime());
        build.setBuildNumber(hudsonBuild.getBuildNumber());

        String hudsonBuildState = hudsonBuild.getState();
        build.setState(State.getStateByName(hudsonBuildState));

        return build;
    }

    private Set<Commiter> createCommiters(Set<HudsonCommiter> hudsonCommiters) {
        Set<Commiter> commiters = new HashSet<Commiter>();
        for (HudsonCommiter hudsonCommiter : hudsonCommiters) {
            commiters.add(createCommiter(hudsonCommiter));
        }
        return commiters;
    }

    private Commiter createCommiter(HudsonCommiter hudsonCommiter) {
        Commiter commiter = new Commiter(hudsonCommiter.getId());
        commiter.setEmail(hudsonCommiter.getEmail());
        commiter.setName(hudsonCommiter.getName());
        return commiter;
    }

    private TestResult createTestResult(HudsonTestResult hudsonTestResult) {
        TestResult testResult = new TestResult();
        testResult.setFailCount(hudsonTestResult.getFailCount());
        testResult.setPassCount(hudsonTestResult.getPassCount());
        testResult.setSkipCount(hudsonTestResult.getSkipCount());
        return testResult;
    }
}
