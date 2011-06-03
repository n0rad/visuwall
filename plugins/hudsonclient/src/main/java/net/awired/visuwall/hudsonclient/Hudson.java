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

package net.awired.visuwall.hudsonclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.hudsonclient.builder.HudsonProjectBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelRun;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.helper.HudsonXmlHelper;
import net.awired.visuwall.hudsonclient.helper.MavenHelper;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.UniformInterfaceException;

public class Hudson {

    private static final Logger LOG = LoggerFactory.getLogger(Hudson.class);

    private static final String DEFAULT_STATE = "NEW";

    private HudsonUrlBuilder hudsonUrlBuilder;
    private HudsonFinder hudsonFinder;
    private HudsonProjectFactory hudsonProjectFactory;
    private HudsonJerseyClient hudsonJerseyClient;

    public Hudson(String hudsonUrl) {
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        hudsonJerseyClient = new HudsonJerseyClient();
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder, hudsonJerseyClient);
        hudsonProjectFactory = new HudsonProjectFactory();
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url " + hudsonUrl);
        }
    }

    public Hudson(String hudsonUrl, HudsonUrlBuilder hudsonUrlBuilder, HudsonFinder hudsonFinder,
            HudsonProjectFactory hudsonProjectFactory, HudsonJerseyClient hudsonJerseyClient) {
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonFinder = hudsonFinder;
        this.hudsonProjectFactory = hudsonProjectFactory;
        this.hudsonJerseyClient = hudsonJerseyClient;
    }

    /**
     * @return List of all available projects on Hudson
     */
    public List<HudsonProject> findAllProjects() {
        List<HudsonProject> projects = new ArrayList<HudsonProject>();
        List<Object> findHudsonProjects = hudsonFinder.findHudsonProjects();
        for (int i = 0; i < findHudsonProjects.size(); i++) {
            Object project = findHudsonProjects.get(i);
            org.w3c.dom.Element element = (org.w3c.dom.Element) project;
            String name = HudsonXmlHelper.getProjectName(element);
            try {
                HudsonProject hudsonProject = findProject(name);
                projects.add(hudsonProject);
            } catch (HudsonProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't add project with name [" + name + "]. cause:" + e.getMessage());
                }
            }
        }
        return projects;
    }

    /**
     * @param projectName
     * @param buildNumber
     * @return HudsonBuild found in Hudson with its project name and build number
     * @throws HudsonBuildNotFoundException
     * @throws HudsonProjectNotFoundException
     */
    public HudsonBuild findBuild(String projectName, int buildNumber) throws HudsonBuildNotFoundException,
            HudsonProjectNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");

        return hudsonFinder.find(projectName, buildNumber);
    }

    /**
     * @param projectName
     * @return HudsonProject found with its name
     * @throws HudsonProjectNotFoundException
     */
    public HudsonProject findProject(String projectName) throws HudsonProjectNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
        try {
            HudsonMavenMavenModuleSet moduleSet = findJobByProjectName(projectName);
            HudsonProjectBuilder hudsonProjectBuilder = hudsonProjectFactory.create(hudsonUrlBuilder, hudsonFinder);
            return hudsonProjectBuilder.createHudsonProjectFrom(moduleSet);
        } catch (HudsonBuildNotFoundException e) {
            throw new HudsonProjectNotFoundException(e);
        }
    }

    private HudsonMavenMavenModuleSet findJobByProjectName(String projectName) throws HudsonProjectNotFoundException {
        try {
            String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
            if (MavenHelper.isNotMavenProject(projectUrl))
                throw new HudsonProjectNotFoundException(projectName + " is not a maven project");
            HudsonMavenMavenModuleSet moduleSet = hudsonJerseyClient.getModuleSet(projectUrl);
            return moduleSet;
        } catch (UniformInterfaceException e) {
            throw new HudsonProjectNotFoundException(e);
        }
    }

    /**
     * If there is no success job in history, the average build duration time is the max duration time Else we compute
     * the average success build duration
     * 
     * @param projectName
     * @return Average build duration time computed with old successful jobs
     * @throws HudsonProjectNotFoundException
     */
    public long getAverageBuildDurationTime(String projectName) throws HudsonProjectNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");

        HudsonProject hudsonProject = findProject(projectName);

        long averageTime;

        if (isNeverSuccessful(hudsonProject)) {
            averageTime = maxDuration(hudsonProject);
        } else {
            averageTime = computeAverageBuildDuration(hudsonProject);
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug("Average build time of " + projectName + " is " + averageTime + " ms");
        }

        return averageTime;
    }

    private long computeAverageBuildDuration(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        String projectName = hudsonProject.getName();
        float sumBuildDurationTime = 0;
        int[] buildNumbers = hudsonProject.getBuildNumbers();

        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(projectName, buildNumber);
                if (build.isSuccessful()) {
                    sumBuildDurationTime += build.getDuration();
                }
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }

        return (long) (sumBuildDurationTime / buildNumbers.length);
    }

    private long maxDuration(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        long max = 0;
        int[] buildNumbers = hudsonProject.getBuildNumbers();

        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(hudsonProject.getName(), buildNumber);
                max = Math.max(max, build.getDuration());
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }

        return max;
    }

    private boolean isNeverSuccessful(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        int[] buildNumbers = hudsonProject.getBuildNumbers();
        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(hudsonProject.getName(), buildNumber);
                if (build.isSuccessful()) {
                    return false;
                }
            } catch (HudsonBuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return true;
    }

    private boolean getIsBuilding(HudsonModelJob modelJob) {
        String color = modelJob.getColor().value();
        return color.endsWith("_anime");
    }

    /**
     * @param hudsonProject
     * @return An array of successful build numbers
     * @throws HudsonProjectNotFoundException
     */
    public int[] getSuccessfulBuildNumbers(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        List<Integer> successfulBuildNumbers = new ArrayList<Integer>();
        for (Integer buildNumber : hudsonProject.getBuildNumbers()) {
            try {
                HudsonBuild build = findBuild(hudsonProject.getName(), buildNumber);
                if (build.isSuccessful()) {
                    successfulBuildNumbers.add(buildNumber);
                }
            } catch (HudsonBuildNotFoundException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        int[] successfulBuilds = new int[successfulBuildNumbers.size()];
        for (int i = 0; i < successfulBuildNumbers.size(); i++) {
            successfulBuilds[i] = successfulBuildNumbers.get(i);
        }
        return successfulBuilds;
    }

    /**
     * @param projectName
     * @return Date which we think the project will finish to build
     * @throws HudsonProjectNotFoundException
     */
    public Date getEstimatedFinishTime(String projectName) throws HudsonProjectNotFoundException {
        HudsonProject project = findProject(projectName);
        HudsonBuild lastBuild = project.getCurrentBuild();
        Date startTime = lastBuild.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(projectName);
        DateTime estimatedFinishTime = new DateTime(startTime.getTime()).plus(averageBuildDurationTime);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Estimated finish time of project " + projectName + " is " + estimatedFinishTime + " ms");
        }

        return estimatedFinishTime.toDate();
    }

    public boolean isBuilding(String projectName) throws HudsonProjectNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
        HudsonModelJob job = findJobByProjectName(projectName);
        return getIsBuilding(job);
    }

    public String getState(String projectName) throws HudsonProjectNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
        try {
            int lastBuildNumber = getLastBuildNumber(projectName);
            HudsonMavenMavenModuleSetBuild build = hudsonFinder.findBuildByProjectNameAndBuildNumber(projectName,
                    lastBuildNumber);
            String state = HudsonXmlHelper.getState(build);
            if ("FAILURE".equals(state) && hasPassedTests(projectName)) {
                state = "UNSTABLE";
            }
            return state;
        } catch (HudsonProjectNotFoundException e) {
            throw new HudsonProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            return DEFAULT_STATE;
        }
    }

    private boolean hasPassedTests(String projectName) throws HudsonProjectNotFoundException {
        HudsonProject project = findProject(projectName);
        HudsonBuild build = project.getCompletedBuild();
        if (build != null) {
            TestResult unitTestResult = build.getUnitTestResult();
            TestResult integrationTestResult = build.getIntegrationTestResult();
            int passedUnitTests = unitTestResult == null ? 0 : unitTestResult.getPassCount();
            int passedIntegrationTests = integrationTestResult == null ? 0 : integrationTestResult.getPassCount();
            return (passedUnitTests + passedIntegrationTests) > 0;
        }
        return false;
    }

    public int getLastBuildNumber(String projectName) throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
        HudsonMavenMavenModuleSet job = findJobByProjectName(projectName);
        HudsonModelRun run = job.getLastBuild();
        if (run == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return run.getNumber();
    }

    public List<String> findProjectNames() {
        return hudsonFinder.findProjectNames();
    }
}