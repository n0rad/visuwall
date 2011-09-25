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

package net.awired.visuwall.plugin.teamcity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.awired.clients.teamcity.TeamCity;
import net.awired.clients.teamcity.exception.TeamCityBuildListNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityBuildNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityChangesNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityProjectNotFoundException;
import net.awired.clients.teamcity.exception.TeamCityProjectsNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityBuild;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityChange;
import net.awired.clients.teamcity.resource.TeamCityProject;
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
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class TeamCityConnection implements BuildCapability, TestCapability {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityConnection.class);

    private boolean connected;

    TeamCity teamCity;

    private String url;

    @Override
    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        this.url = url;
        teamCity = new TeamCity(url);
        connected = true;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityProject project = teamCity.findProject(projectId);
            return project.getDescription();
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find description of project with software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(projectKey, "projectKey is mandatory");
        try {
            String name = projectKey.getName();
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String projectName = project.getName();
                if (projectName.equals(name)) {
                    String projectId = project.getId();
                    return new SoftwareProjectId(projectId);
                }
            }
        } catch (TeamCityProjectsNotFoundException e) {
            throw new ProjectNotFoundException("Can't identify software project id with project key: " + projectKey,
                    e);
        }
        throw new ProjectNotFoundException("Can't identify software project id with project key: " + projectKey);
    }

    @Override
    public List<Integer> getBuildNumbers(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            Set<Integer> numbers = new TreeSet<Integer>();
            List<TeamCityBuildType> buildTypes = getBuildTypesFrom(softwareProjectId);
            for (TeamCityBuildType buildType : buildTypes) {
                addBuildNumbers(numbers, buildType);
            }
            return new ArrayList<Integer>(numbers);
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException(
                    "Can't find build numbers of software project id:" + softwareProjectId, e);
        }
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projectIds = new HashMap<SoftwareProjectId, String>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String id = project.getId();
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(id);
                projectIds.put(softwareProjectId, project.getName());
            }
        } catch (TeamCityProjectsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't build list of software project ids.", e);
            }
        }
        return projectIds;
    }

    @Override
    public State getBuildState(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String projectId = softwareProjectId.getProjectId();
            String strBuildNumber = buildNumber.toString();
            TeamCityBuild build = teamCity.findBuild(projectId, strBuildNumber);
            String status = build.getStatus();
            return States.asVisuwallState(status);
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project for software project id:" + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " for software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityBuild build = teamCity.findRunningBuild();
            TeamCityBuildType buildType = build.getBuildType();
            return projectId.equals(buildType.getProjectId()) && buildNumber.toString().equals(build.getNumber());
        } catch (TeamCityBuildNotFoundException e) {
            return false;
        }
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            TeamCityBuild runningBuild = teamCity.findRunningBuild();
            return Integer.parseInt(runningBuild.getNumber());
        } catch (TeamCityBuildNotFoundException e) {
            List<Integer> buildNumbers = getBuildNumbers(softwareProjectId);
            if (buildNumbers.isEmpty()) {
                throw new BuildNumberNotFoundException("Can't find build numbers for software project id : "
                        + softwareProjectId);
            }
            Integer lastBuildNumber = Collections.max(buildNumbers);
            return lastBuildNumber;
        }
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        throw new MavenIdNotFoundException("TeamCity does not implemented this capability");
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityProject project = teamCity.findProject(projectId);
            return project.getName();
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of project with software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String projectId = softwareProjectId.getProjectId();
            String strBuildNumber = buildNumber.toString();
            TeamCityBuild teamcityBuild = teamCity.findBuild(projectId, strBuildNumber);
            return BuildTimes.createFrom(teamcityBuild);
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of project with software project id:"
                    + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " for software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityProject project = teamCity.findProject(projectId);
            return project.isArchived();
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id:" + softwareProjectId, e);
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
            List<TeamCityChange> changes = teamCity.findChanges(buildNumber);
            for (TeamCityChange change : changes) {
                String username = change.getUsername();
                Commiter commiter = new Commiter(username);
                commiter.setName(username);
                if (!commiters.contains(commiter)) {
                    commiters.add(commiter);
                }
            }
            return commiters;
        } catch (TeamCityChangesNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        return commiters;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId softwareProjectId) {
        TestResult result = new TestResult();
        try {
            Integer lastBuildNumber = getLastBuildNumber(softwareProjectId);
            TeamCityBuild build = teamCity.findBuild(softwareProjectId.getProjectId(), lastBuildNumber.toString());
            String statusText = build.getStatusText();
            int failed = TestResultExtractor.extractFailed(statusText);
            int passed = TestResultExtractor.extractPassed(statusText);
            int ignored = TestResultExtractor.extractIgnored(statusText);
            result.setFailCount(failed);
            result.setPassCount(passed);
            result.setSkipCount(ignored);
        } catch (ProjectNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (BuildNumberNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (TeamCityProjectNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        }
        return result;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId softwareProjectId) {
        return new TestResult();
    }

    private void addBuildNumbers(Set<Integer> numbers, TeamCityBuildType buildType) {
        try {
            String buildTypeId = buildType.getId();
            TeamCityBuilds buildList = teamCity.findBuildList(buildTypeId);
            List<TeamCityBuildItem> builds = buildList.getBuilds();
            for (TeamCityBuildItem item : builds) {
                addBuildNumbers(numbers, item);
            }
        } catch (TeamCityBuildListNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

    private void addBuildNumbers(Set<Integer> numbers, TeamCityBuildItem item) {
        try {
            int number = Integer.parseInt(item.getNumber());
            numbers.add(number);
        } catch (NumberFormatException e) {
            if (LOG.isTraceEnabled()) {
                LOG.trace("Can't transform in a valid build number", e);
            }
        }
    }

    private List<TeamCityBuildType> getBuildTypesFrom(SoftwareProjectId softwareProjectId)
            throws TeamCityProjectNotFoundException {
        String projectId = softwareProjectId.getProjectId();
        TeamCityProject project = teamCity.findProject(projectId);
        return project.getBuildTypes();
    }

    private void checkBuildNumber(int buildNumber) {
        Preconditions.checkArgument(buildNumber >= 0, "buildNumber must be >= 0");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

}
