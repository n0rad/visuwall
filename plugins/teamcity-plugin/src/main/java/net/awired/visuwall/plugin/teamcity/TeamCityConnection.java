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
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildListNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildItem;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildType;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class TeamCityConnection implements BuildCapability {

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
    public List<String> findProjectNames() {
        checkConnected();
        try {
            return teamCity.findProjectNames();
        } catch (TeamCityProjectsNotFoundException e) {
            return new ArrayList<String>();
        }
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
                    SoftwareProjectId softwareProjectId = new SoftwareProjectId(projectId);
                    return softwareProjectId;
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
            String projectId = softwareProjectId.getProjectId();
            TeamCityProject project = teamCity.findProject(projectId);
            List<TeamCityBuildType> buildTypes = project.getBuildTypes();
            Set<Integer> numbers = new TreeSet<Integer>();
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
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String id = project.getId();
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(id);
                projectIds.add(softwareProjectId);
            }
        } catch (TeamCityProjectsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't build list of software project ids.", e);
            }
        }
        return projectIds;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String name = project.getName();
                if (names.contains(name)) {
                    String id = project.getId();
                    SoftwareProjectId projectId = new SoftwareProjectId(id);
                    projectIds.add(projectId);
                }
            }
        } catch (TeamCityProjectsNotFoundException e) {
            LOG.warn("Can't find projects by name with this Team City connection," + this.url, e);
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
            TeamCityBuild build = findBuild(softwareProjectId, buildNumber);
            String status = build.getStatus();
            State state = States.asVisuwallState(status);
            return state;
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
            TeamCityBuild build = findBuild(softwareProjectId, buildNumber);
            if (build == null) {
                throw new BuildNotFoundException("Can't find build #" + buildNumber + " for software project id:"
                        + softwareProjectId);
            }
            return build.getFinishDate().after(new Date());
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project for software project id:" + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " for software project id:"
                    + softwareProjectId, e);
        }
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        List<Integer> buildNumbers = getBuildNumbers(softwareProjectId);
        Integer lastBuildNumber = Collections.max(buildNumbers);
        return lastBuildNumber;
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
            TeamCityBuild teamcityBuild = findBuild(softwareProjectId, buildNumber);
            BuildTime buildTime = new BuildTime();
            Date finishDate = teamcityBuild.getFinishDate();
            Date startDate = teamcityBuild.getStartDate();
            long duration = finishDate.getTime() - startDate.getTime();
            buildTime.setDuration(duration);
            buildTime.setStartTime(startDate);
            return buildTime;
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of project with software project id:"
                    + softwareProjectId, e);
        } catch (TeamCityBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " for software project id:"
                    + softwareProjectId, e);
        }
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
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't transform in a valid build number", e);
            }
        }
    }

    private TeamCityBuild findBuild(SoftwareProjectId softwareProjectId, int buildNumber)
            throws TeamCityProjectNotFoundException, TeamCityBuildNotFoundException {
        String buildNumberAsString = Integer.toString(buildNumber);
        String projectId = softwareProjectId.getProjectId();
        TeamCityProject project = teamCity.findProject(projectId);
        List<TeamCityBuildType> buildTypes = project.getBuildTypes();
        for (TeamCityBuildType buildType : buildTypes) {
            try {
                String buildTypeId = buildType.getId();
                TeamCityBuilds buildList = teamCity.findBuildList(buildTypeId);
                List<TeamCityBuildItem> builds = buildList.getBuilds();
                for (TeamCityBuildItem buildItem : builds) {
                    if (buildItem.getNumber().equals(buildNumberAsString)) {
                        String buildId = buildItem.getId();
                        int buildIdAsString = Integer.parseInt(buildId);
                        return teamCity.findBuild(buildIdAsString);
                    }
                }
            } catch (TeamCityBuildListNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        throw new TeamCityBuildNotFoundException("Can't find build #" + buildNumber + "of software project id "
                + softwareProjectId);
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
