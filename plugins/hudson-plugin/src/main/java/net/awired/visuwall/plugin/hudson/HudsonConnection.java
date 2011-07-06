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

import static org.apache.commons.lang.StringUtils.isBlank;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import net.awired.clients.hudson.Hudson;
import net.awired.clients.hudson.domain.HudsonBuild;
import net.awired.clients.hudson.domain.HudsonCommiter;
import net.awired.clients.hudson.domain.HudsonJob;
import net.awired.clients.hudson.domain.HudsonTestResult;
import net.awired.clients.hudson.exception.ArtifactIdNotFoundException;
import net.awired.clients.hudson.exception.HudsonBuildNotFoundException;
import net.awired.clients.hudson.exception.HudsonJobNotFoundException;
import net.awired.clients.hudson.exception.HudsonViewNotFoundException;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class HudsonConnection implements BuildCapability, ViewCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonConnection.class);

    @VisibleForTesting
    Hudson hudson;

    private boolean connected;

    @Override
    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        hudson = new Hudson(url);
        connected = true;
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<HudsonJob> projects = hudson.findAllProjects();
        for (int i = 0; i < projects.size(); i++) {
            HudsonJob hudsonProject = projects.get(i);
            SoftwareProjectId projectId = new SoftwareProjectId(hudsonProject.getName());
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = jobName(projectId);
            return hudson.getEstimatedFinishTime(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, Integer buildNumber) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = jobName(projectId);
            return hudson.isBuilding(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public State getBuildState(SoftwareProjectId projectId, Integer buildNumber) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = jobName(projectId);
            HudsonBuild hudsonBuild = hudson.findBuild(projectName, buildNumber);
            String hudsonState = hudsonBuild.getState();
            return States.asVisuwallState(hudsonState);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String projectName = jobName(projectId);
            return hudson.getLastBuildNumber(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNumberNotFoundException(e);
        }
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        return hudson.findJobNames();
    }

    @Override
    public List<String> findViews() {
        checkConnected();
        return hudson.findViews();
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
        try {
            return hudson.findJobNameByView(viewName);
        } catch (HudsonViewNotFoundException e) {
            throw new ViewNotFoundException("can't find view named :" + viewName, e);
        }
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        checkConnected();
        Preconditions.checkNotNull(views, "views is mandatory");
        Set<SoftwareProjectId> projectIds = new HashSet<SoftwareProjectId>();
        for (String viewName : views) {
            try {
                List<String> projectNames = hudson.findJobNameByView(viewName);
                projectIds.addAll(findSoftwareProjectIdsByNames(projectNames));
            } catch (HudsonViewNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<SoftwareProjectId>(projectIds);
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        for (String name : names) {
            SoftwareProjectId projectId = new SoftwareProjectId(name);
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String jobName = jobName(projectId);
            return hudson.getDescription(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find description of project id: " + projectId, e);
        }
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(projectKey, "projectKey is mandatory");
        String jobName = projectKey.getName();
        if (jobName != null) {
            try {
                hudson.findJob(jobName);
                return new SoftwareProjectId(jobName);
            } catch (HudsonJobNotFoundException e) {
                throw new ProjectNotFoundException("Can't identify job with project key: " + projectKey, e);
            }
        }
        throw new ProjectNotFoundException("Can't identify job with project key: " + projectKey);
    }

    @Override
    public List<Integer> getBuildNumbers(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        try {
            String jobName = softwareProjectId.getProjectId();
            return hudson.getBuildNumbers(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(
                    "Can't find build numbers of software project id " + softwareProjectId, e);
        }
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            return hudson.findArtifactId(jobName);
        } catch (ArtifactIdNotFoundException e) {
            throw new MavenIdNotFoundException("Can't get maven id of project " + softwareProjectId, e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectName = softwareProjectId.getProjectId();
            HudsonJob project = hudson.findJob(projectName);
            return project.getName();
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't get name of project " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonBuild hudsonBuild = hudson.findBuild(jobName, buildNumber);
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(hudsonBuild.getDuration());
            buildTime.setStartTime(hudsonBuild.getStartTime());
            return buildTime;
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " of project " + softwareProjectId,
                    e);
        } catch (HudsonJobNotFoundException e) {
            throw new BuildNotFoundException("Can't find project " + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonJob job = hudson.findJob(jobName);
            return job.isDisabled();
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find job with software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        List<Commiter> commiters = new ArrayList<Commiter>();
        try {
            String jobName = softwareProjectId.getProjectId();
            HudsonBuild build = hudson.findBuild(jobName, buildNumber);
            Set<HudsonCommiter> commiterSet = build.getCommiters();
            for (HudsonCommiter hudsonCommiter : commiterSet) {
                Commiter commiter = new Commiter(hudsonCommiter.getId());
                commiter.setName(hudsonCommiter.getName());
                commiter.setEmail(hudsonCommiter.getEmail());
                if (!commiters.contains(commiter)) {
                    commiters.add(commiter);
                }
            }
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find job with software project id: " + softwareProjectId, e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build with software project id: " + softwareProjectId
                    + " and buildNumber: " + buildNumber, e);
        }
        return commiters;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId softwareProjectId) {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            int lastBuildNumber = hudson.getLastBuildNumber(jobName);
            HudsonTestResult unitTestResult = hudson.findUnitTestResult(jobName, lastBuildNumber);
            return TestResults.createFrom(unitTestResult);
        } catch (HudsonJobNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        } catch (HudsonBuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        }
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId softwareProjectId) {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String jobName = softwareProjectId.getProjectId();
            int lastBuildNumber = hudson.getLastBuildNumber(jobName);
            HudsonTestResult integrationTestResult = hudson.findIntegrationTestResult(jobName, lastBuildNumber);
            return TestResults.createFrom(integrationTestResult);
        } catch (HudsonJobNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        } catch (HudsonBuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't analyze integration test of project: " + softwareProjectId, e);
            }
            return new TestResult();
        }
    }

    private void checkBuildNumber(Integer buildNumber) {
        Preconditions.checkNotNull(buildNumber, "buildNumber is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    private String jobName(SoftwareProjectId softwareProjectId) throws HudsonJobNotFoundException {
        String jobName = softwareProjectId.getProjectId();
        if (jobName == null) {
            throw new HudsonJobNotFoundException("Project id " + softwareProjectId + " does not contain id");
        }
        return jobName;
    }

}
