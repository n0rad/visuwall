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

package net.awired.visuwall.hudsonclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonViewNotFoundException;
import net.awired.visuwall.hudsonclient.finder.HudsonFinder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class Hudson {

    private static final Logger LOG = LoggerFactory.getLogger(Hudson.class);

    private static final String DEFAULT_STATE = "UNKNOWN";

    private HudsonFinder hudsonFinder;

    public Hudson(String hudsonUrl) {
        HudsonUrlBuilder hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder);
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url " + hudsonUrl);
        }
    }

    public Hudson(HudsonFinder hudsonFinder) {
        this.hudsonFinder = hudsonFinder;
    }

    /**
     * @return List of all available projects on Hudson
     */
    public List<HudsonProject> findAllProjects() {
        List<HudsonProject> projects = new ArrayList<HudsonProject>();
        for (String projectName : hudsonFinder.findProjectNames()) {
            try {
                HudsonProject hudsonProject = findProject(projectName);
                projects.add(hudsonProject);
            } catch (HudsonProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't add project with name [" + projectName + "]. cause:" + e.getMessage());
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
        checkProjectName(projectName);
        return hudsonFinder.find(projectName, buildNumber);
    }

    private void checkProjectName(String projectName) {
        Preconditions.checkNotNull(projectName, "projectName is mandatory");
    }

    /**
     * @param projectName
     * @return HudsonProject found with its name
     * @throws HudsonProjectNotFoundException
     */
    public HudsonProject findProject(String projectName) throws HudsonProjectNotFoundException {
        checkProjectName(projectName);
        return hudsonFinder.findProject(projectName);
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
        checkProjectName(projectName);
        HudsonProject hudsonProject = findProject(projectName);
        return computeBuildDurationTime(hudsonProject);
    }

    private long computeBuildDurationTime(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        long averageTime;
        if (isNeverSuccessful(hudsonProject)) {
            averageTime = maxDuration(hudsonProject);
        } else {
            averageTime = computeAverageBuildDuration(hudsonProject);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Average build time of " + hudsonProject.getName() + " is " + averageTime + " ms");
        }
        return averageTime;
    }

    /**
     * @param projectName
     * @return Date which we think the project will finish to build
     * @throws HudsonProjectNotFoundException
     */
    public Date getEstimatedFinishTime(String projectName) throws HudsonProjectNotFoundException {
        HudsonProject project = findProject(projectName);
        HudsonBuild currentBuild = project.getCurrentBuild();
        if (currentBuild == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(projectName + " has no current build");
            }
            return new Date();
        }
        long averageBuildDurationTime = computeBuildDurationTime(project);
        Date startTime = currentBuild.getStartTime();
        if (startTime == null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(currentBuild + " has no start time");
            }
            return new Date();
        }
        long time = startTime.getTime();
        DateTime dateTime = new DateTime(time);
        DateTime estimatedFinishTime = dateTime.plus(averageBuildDurationTime);
        return estimatedFinishTime.toDate();
    }

    public boolean isBuilding(String projectName) throws HudsonProjectNotFoundException {
        checkProjectName(projectName);
        return hudsonFinder.isBuilding(projectName);
    }

    public String getState(String projectName) throws HudsonProjectNotFoundException {
        checkProjectName(projectName);
        String state = DEFAULT_STATE;
        try {
            int lastBuildNumber = getLastBuildNumber(projectName);
            state = hudsonFinder.getStateOf(projectName, lastBuildNumber);
            if ("FAILURE".equals(state) && hasPassedTests(projectName)) {
                state = "UNSTABLE";
            }
        } catch (HudsonBuildNotFoundException e) {
            //TODO should return an error not a state
            state = DEFAULT_STATE;
        }
        return state;
    }

    public int getLastBuildNumber(String projectName) throws HudsonProjectNotFoundException,
            HudsonBuildNotFoundException {
        checkProjectName(projectName);
        return hudsonFinder.getLastBuildNumber(projectName);
    }

    public List<String> findProjectNames() {
        List<String> projectNames = hudsonFinder.findProjectNames();
        return projectNames;
    }

    public List<String> findViews() {
        return hudsonFinder.findViews();
    }

    public List<String> findProjectNameByView(String viewName) throws HudsonViewNotFoundException {
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
        return hudsonFinder.findProjectNamesByView(viewName);
    }

    private boolean hasPassedTests(String projectName) throws HudsonProjectNotFoundException {
        HudsonProject project = findProject(projectName);
        HudsonBuild build = project.getCompletedBuild();
        if (build != null) {
            HudsonTestResult unitTestResult = build.getUnitTestResult();
            HudsonTestResult integrationTestResult = build.getIntegrationTestResult();
            int passedUnitTests = unitTestResult == null ? 0 : unitTestResult.getPassCount();
            int passedIntegrationTests = integrationTestResult == null ? 0 : integrationTestResult.getPassCount();
            return (passedUnitTests + passedIntegrationTests) > 0;
        }
        return false;
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

}
