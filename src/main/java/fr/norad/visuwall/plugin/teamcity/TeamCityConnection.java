/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.plugin.teamcity;

import static com.google.common.base.Preconditions.checkNotNull;
import static com.google.common.base.Preconditions.checkState;
import static org.apache.commons.lang.StringUtils.isBlank;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.domain.TestResult;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.exception.ViewNotFoundException;
import fr.norad.visuwall.plugin.capability.BuildCapability;
import fr.norad.visuwall.plugin.capability.TestCapability;
import fr.norad.visuwall.plugin.capability.ViewCapability;
import fr.norad.visuwall.providers.teamcity.TeamCity;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityBuildListNotFoundException;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityBuildNotFoundException;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityBuildTypeNotFoundException;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityChangesNotFoundException;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityProjectNotFoundException;
import fr.norad.visuwall.providers.teamcity.exception.TeamCityProjectsNotFoundException;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityAbstractBuild;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuild;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuildItem;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuildType;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityBuilds;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityChange;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityProject;

public class TeamCityConnection implements BuildCapability, TestCapability, ViewCapability {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityConnection.class);

    private boolean connected;

    private TestResultExtractor testResultExtractor = new TestResultExtractor();

    @VisibleForTesting
    TeamCity teamCity;

    @Override
    public void connect(String url, String login, String password) {
        checkNotNull(url, "url is mandatory");
        if (isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        if (isBlank(login)) {
            LOG.info("Login is blank, new value is 'guest'");
            login = "guest";
            password = "";
        }
        teamCity = new TeamCity(url, login, password);
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
            String buildTypeId = softwareProjectId.getProjectId();
            TeamCityBuildType buildType = teamCity.findBuildType(buildTypeId);
            return buildType.getDescription();
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find description of project with software project id:"
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
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            Set<String> ids = new TreeSet<String>();
            TeamCityBuildType buildType = teamCity.findBuildType(softwareProjectId.getProjectId());
            addBuildIds(ids, buildType);
            List<String> arrayList = new ArrayList<String>(ids);
            Collections.sort(arrayList, new BuildIdComparator());
            return arrayList;
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find build numbers of software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projectIds = new HashMap<SoftwareProjectId, String>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                try {
                    project = teamCity.findProject(project.getId());
                    List<TeamCityBuildType> buildTypes = project.getBuildTypes();
                    for (TeamCityBuildType teamCityBuildType : buildTypes) {
                        String id = teamCityBuildType.getId();
                        SoftwareProjectId softwareProjectId = new SoftwareProjectId(id);
                        projectIds.put(softwareProjectId, teamCityBuildType.getName());
                    }
                } catch (TeamCityProjectNotFoundException e) {
                    LOG.warn("Cannot find project with id " + project.getId(), e);
                }
            }
        } catch (TeamCityProjectsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Cannot build list of software project ids.", e);
            }
        }
        return projectIds;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId softwareProjectId, String buildId)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityBuild build = teamCity.findBuild(projectId, buildId);
            String status = build.getStatus();
            return States.asVisuwallState(status);
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException("Cannot find build type for software project id:" + softwareProjectId,
                    e);
        } catch (TeamCityBuildNotFoundException e) {
            try {
                TeamCityBuilds runningBuilds = teamCity.findRunningBuilds();
                for (TeamCityBuildItem runningBuild : runningBuilds.getBuilds()) {
                    if (buildId.equals(runningBuild.getId())) {
                        return BuildState.UNKNOWN;
                    }
                }
            } catch (TeamCityBuildNotFoundException e1) {
            }
            throw new BuildNotFoundException("Cannot find build #" + buildId + " for software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId softwareProjectId, String buildId)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            TeamCityBuild runningBuild = teamCity.findBuild(Integer.valueOf(buildId));
            int seconds = runningBuild.getRunningInfo().getEstimatedTotalSeconds();
            return new DateTime().plusSeconds(seconds).toDate();
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Cannot find a running build", e);
        }
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        String projectId = softwareProjectId.getProjectId();
        try {
            List<TeamCityBuildItem> builds = teamCity.findRunningBuilds().getBuilds();
            for (TeamCityBuildItem build : builds) {
                if (build.is(projectId, buildId)) {
                    return true;
                }
            }
        } catch (TeamCityBuildNotFoundException e) {
            return false;
        }
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        TeamCityAbstractBuild lastBuild;
        try {
            lastBuild = teamCity.findLastBuild(softwareProjectId.getProjectId());
            return lastBuild.getId();
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildIdNotFoundException("Cannot find project with software project id " + softwareProjectId, e);
        }
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String projectId = softwareProjectId.getProjectId();
        try {
            return teamCity.findMavenId(projectId);
        } catch (MavenIdNotFoundException e) {
            throw new MavenIdNotFoundException("Cannot find maven id for " + softwareProjectId, e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String buildTypeId = softwareProjectId.getProjectId();
            TeamCityBuildType buildType = teamCity.findBuildType(buildTypeId);
            return buildType.getName();
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of project with software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityBuild teamcityBuild = teamCity.findBuild(projectId, buildId);
            return BuildTimes.createFrom(teamcityBuild);
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException(
                    "Cannot find build type with software project id:" + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Cannot find build #" + buildId + " for software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectId = softwareProjectId.getProjectId();
            TeamCityBuildType buildType = teamCity.findBuildType(projectId);
            return buildType.isPaused();
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id:" + softwareProjectId, e);
        }
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        List<Commiter> commiters = new ArrayList<Commiter>();
        try {
            List<TeamCityChange> changes = teamCity.findChanges(Integer.valueOf(buildId));
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
        checkConnected();
        TestResult result = new TestResult();
        try {
            String lastBuildId = getLastBuildId(softwareProjectId);
            if (isBuilding(softwareProjectId, lastBuildId)) {
                return result;
            }
            TeamCityBuild build = teamCity.findBuild(softwareProjectId.getProjectId(), lastBuildId.toString());
            String statusText = build.getStatusText();
            int failed = testResultExtractor.extractFailed(statusText);
            int passed = testResultExtractor.extractPassed(statusText);
            int ignored = testResultExtractor.extractIgnored(statusText);
            result.setFailCount(failed);
            result.setPassCount(passed);
            result.setSkipCount(ignored);
        } catch (ProjectNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (BuildIdNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (TeamCityBuildTypeNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        } catch (BuildNotFoundException e) {
            LOG.warn("Can't analyze unit tests for softwareProjectId:" + softwareProjectId, e);
        }
        return result;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId softwareProjectId) {
        checkConnected();
        return new TestResult();
    }

    private void addBuildIds(Set<String> numbers, TeamCityBuildType buildType) {
        try {
            String buildTypeId = buildType.getId();
            TeamCityBuilds buildList = teamCity.findBuildList(buildTypeId);
            List<TeamCityBuildItem> builds = buildList.getBuilds();
            for (TeamCityBuildItem item : builds) {
                numbers.add(item.getId());
            }
        } catch (TeamCityBuildListNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

    private void checkBuildId(String buildId) {
        checkNotNull("buildId is mandatory");
    }

    private void checkConnected() {
        checkState(connected, "You must connect your plugin");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        checkConnected();
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        for (String viewName : views) {
            try {
                TeamCityProject project = teamCity.findProjectByName(viewName);
                project = teamCity.findProject(project.getId());
                List<TeamCityBuildType> buildTypes = project.getBuildTypes();
                for (TeamCityBuildType teamCityBuildType : buildTypes) {
                    SoftwareProjectId softwareProjectId = new SoftwareProjectId(teamCityBuildType.getId());
                    softwareProjectIds.add(softwareProjectId);
                }
            } catch (TeamCityProjectNotFoundException e) {
                LOG.warn("Cannot add build types of project " + viewName, e);
            }
        }
        return softwareProjectIds;
    }

    @Override
    public List<String> findViews() {
        checkConnected();
        List<String> views = new ArrayList<String>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String view = project.getName();
                views.add(view);
            }
        } catch (TeamCityProjectsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't build list of software project ids.", e);
            }
        }
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        checkConnected();
        checkViewName(viewName);
        List<String> projects = new ArrayList<String>();
        try {
            TeamCityProject project = teamCity.findProjectByName(viewName);
            project = teamCity.findProject(project.getId());
            List<TeamCityBuildType> buildTypes = project.getBuildTypes();
            for (TeamCityBuildType buildType : buildTypes) {
                String projectName = buildType.getName();
                projects.add(projectName);
            }
        } catch (TeamCityProjectNotFoundException e) {
            throw new ViewNotFoundException("Cannot finnd project names for view: " + viewName, e);
        }
        return projects;
    }

    private void checkViewName(String viewName) {
        checkNotNull(viewName, "viewName is mandatory");
    }

}
