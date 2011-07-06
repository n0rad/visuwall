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
import java.util.Date;
import java.util.List;
import net.awired.clients.hudson.builder.HudsonUrlBuilder;
import net.awired.clients.hudson.domain.HudsonBuild;
import net.awired.clients.hudson.domain.HudsonJob;
import net.awired.clients.hudson.domain.HudsonTestResult;
import net.awired.clients.hudson.exception.ArtifactIdNotFoundException;
import net.awired.clients.hudson.exception.HudsonBuildNotFoundException;
import net.awired.clients.hudson.exception.HudsonJobNotFoundException;
import net.awired.clients.hudson.exception.HudsonViewNotFoundException;
import net.awired.clients.hudson.finder.HudsonFinder;
import net.awired.clients.hudson.finder.HudsonRootModuleFinder;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class Hudson {

    private static final Logger LOG = LoggerFactory.getLogger(Hudson.class);

    @VisibleForTesting
    HudsonFinder hudsonFinder;

    @VisibleForTesting
    HudsonRootModuleFinder hudsonRootModuleFinder;

    public Hudson(String hudsonUrl) {
        HudsonUrlBuilder hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder);
        hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url " + hudsonUrl);
        }
    }

    /**
     * @return List of all available projects on Hudson
     */
    public List<HudsonJob> findAllProjects() {
        List<HudsonJob> projects = new ArrayList<HudsonJob>();
        for (String jobName : hudsonFinder.findJobNames()) {
            try {
                if (findArtifactId(jobName) != null) {
                    HudsonJob hudsonProject = findJob(jobName);
                    projects.add(hudsonProject);
                }
            } catch (HudsonJobNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't add project with name [" + jobName + "]. cause:" + e.getMessage());
                }
            } catch (ArtifactIdNotFoundException e) {
                // we only add maven projects
            }
        }
        return projects;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return HudsonBuild found in Hudson with its project name and build number
     * @throws HudsonBuildNotFoundException
     * @throws HudsonJobNotFoundException
     */
    public HudsonBuild findBuild(String jobName, int buildNumber) throws HudsonBuildNotFoundException,
            HudsonJobNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.find(jobName, buildNumber);
    }

    /**
     * @param jobName
     * @return HudsonJob found with its name
     * @throws HudsonJobNotFoundException
     */
    public HudsonJob findJob(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.findJob(jobName);
    }

    /**
     * Return the description of the project identify by its projectName
     * 
     * @param string
     * @return
     * @throws HudsonJobNotFoundException
     */
    public String getDescription(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        return hudsonFinder.getDescription(jobName);
    }

    /**
     * @param jobName
     * @return Date which we think the project will finish to build
     * @throws HudsonJobNotFoundException
     */
    public Date getEstimatedFinishTime(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        try {
            HudsonJob hudsonJob = hudsonFinder.findJob(jobName);
            HudsonBuild currentBuild = hudsonFinder.getCurrentBuild(jobName);
            if (currentBuild == null) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(jobName + " has no current build");
                }
                return new Date();
            }
            long averageBuildDurationTime = computeBuildDurationTime(hudsonJob);
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
        } catch (HudsonBuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't find estimated finish time of job: " + jobName, e);
            }
        }
        return new Date();
    }

    public boolean isBuilding(String projectName) throws HudsonJobNotFoundException {
        checkJobName(projectName);
        return hudsonFinder.isBuilding(projectName);
    }

    public int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        checkJobName(projectName);
        return hudsonFinder.getLastBuildNumber(projectName);
    }

    public List<String> findJobNames() {
        List<String> jobNames = hudsonFinder.findJobNames();
        List<String> filteredJobNames = new ArrayList<String>();
        for (String jobName : jobNames) {
            try {
                findArtifactId(jobName);
                filteredJobNames.add(jobName);
            } catch (ArtifactIdNotFoundException e) {
                // we only add maven projects
            }
        }
        return filteredJobNames;
    }

    public List<String> findViews() {
        return hudsonFinder.findViews();
    }

    public List<String> findJobNameByView(String viewName) throws HudsonViewNotFoundException {
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
        List<String> jobNames = hudsonFinder.findJobNamesByView(viewName);
        List<String> filteredJobNames = new ArrayList<String>();
        for (String jobName : jobNames) {
            try {
                findArtifactId(jobName);
                filteredJobNames.add(jobName);
            } catch (ArtifactIdNotFoundException e) {
                // we only add maven projects
            }
        }
        return filteredJobNames;
    }

    public String findArtifactId(String jobName) throws ArtifactIdNotFoundException {
        return hudsonRootModuleFinder.findArtifactId(jobName);
    }

    public List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        try {
            return hudsonFinder.getBuildNumbers(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new HudsonJobNotFoundException("Can't find build numbers of jobName '" + jobName + "'", e);
        }
    }

    private long computeBuildDurationTime(HudsonJob hudsonJob) throws HudsonJobNotFoundException {
        long averageTime;
        if (isNeverSuccessful(hudsonJob.getName())) {
            averageTime = maxDuration(hudsonJob);
        } else {
            averageTime = computeAverageBuildDuration(hudsonJob);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug("Average build time of " + hudsonJob.getName() + " is " + averageTime + " ms");
        }
        return averageTime;
    }

    private long maxDuration(HudsonJob hudsonProject) throws HudsonJobNotFoundException {
        long max = 0;
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(hudsonProject.getName());

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

    private boolean isNeverSuccessful(String jobName) throws HudsonJobNotFoundException {
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(jobName);
        for (int buildNumber : buildNumbers) {
            try {
                HudsonBuild build = findBuild(jobName, buildNumber);
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

    private long computeAverageBuildDuration(HudsonJob hudsonJob) throws HudsonJobNotFoundException {
        String projectName = hudsonJob.getName();
        float sumBuildDurationTime = 0;
        List<Integer> buildNumbers = hudsonFinder.getBuildNumbers(projectName);

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

        return (long) (sumBuildDurationTime / buildNumbers.size());
    }

    private void checkJobName(String jobName) {
        Preconditions.checkNotNull(jobName, "jobName is mandatory");
    }

    public HudsonTestResult findUnitTestResult(String jobName, int lastBuildNumber)
            throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        return hudsonFinder.findUnitTestResult(jobName, lastBuildNumber);
    }

    public HudsonTestResult findIntegrationTestResult(String jobName, int lastBuildNumber)
            throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        return hudsonFinder.findIntegrationTestResult(jobName, lastBuildNumber);
    }
}
