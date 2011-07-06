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

package net.awired.clients.teamcity;

import java.util.ArrayList;
import java.util.List;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.teamcity.builder.TeamCityUrlBuilder;
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
import net.awired.clients.teamcity.resource.TeamCityChanges;
import net.awired.clients.teamcity.resource.TeamCityProject;
import net.awired.clients.teamcity.resource.TeamCityProjects;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class TeamCity {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCity.class);

    @VisibleForTesting
    GenericSoftwareClient client;

    @VisibleForTesting
    TeamCityUrlBuilder urlBuilder;

    public TeamCity() {
    }

    public TeamCity(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        this.urlBuilder = new TeamCityUrlBuilder(url);
        this.client = new GenericSoftwareClient("guest", "");
    }

    public List<String> findProjectNames() throws TeamCityProjectsNotFoundException {
        List<String> projectNames = new ArrayList<String>();
        List<TeamCityProject> projects = findAllProjects();
        for (TeamCityProject project : projects) {
            projectNames.add(project.getName());
        }
        return projectNames;
    }

    public List<TeamCityProject> findAllProjects() throws TeamCityProjectsNotFoundException {
        try {
            String projectsUrl = urlBuilder.getProjects();
            TeamCityProjects teamCityProjects = client.resource(projectsUrl, TeamCityProjects.class);
            return teamCityProjects.getProjects();
        } catch (ResourceNotFoundException e) {
            throw new TeamCityProjectsNotFoundException("Projects have not been found", e);
        }
    }

    public TeamCityProject findProject(String projectId) throws TeamCityProjectNotFoundException {
        checkProjectId(projectId);
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
        try {
            String projectUrl = urlBuilder.getProject(projectId);
            TeamCityProject teamCityProject = client.resource(projectUrl, TeamCityProject.class);
            return teamCityProject;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityProjectNotFoundException("Project #" + projectId + " has not been found", e);
        }
    }

    public TeamCityBuild findBuild(int buildId) throws TeamCityBuildNotFoundException {
        checkBuildId(buildId);
        Preconditions.checkArgument(buildId >= 0, "buildId must be >= 0");
        try {
            String buildUrl = urlBuilder.getBuild(buildId);
            TeamCityBuild teamCityBuild = client.resource(buildUrl, TeamCityBuild.class);
            return teamCityBuild;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildNotFoundException("Build #" + buildId + " has not been found", e);
        }
    }

    public TeamCityBuilds findBuildList(String buildTypeId) throws TeamCityBuildListNotFoundException {
        Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
        try {
            String buildListUrl = urlBuilder.getBuildList(buildTypeId);
            TeamCityBuilds teamCityBuilds = client.resource(buildListUrl, TeamCityBuilds.class);
            return teamCityBuilds;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildListNotFoundException("Build list of buildTypeId " + buildTypeId
                    + " has not been found", e);
        }
    }

    public TeamCityBuild findBuild(String projectId, String buildNumber) throws TeamCityProjectNotFoundException,
            TeamCityBuildNotFoundException {
        TeamCityProject project = findProject(projectId);
        List<TeamCityBuildType> buildTypes = project.getBuildTypes();
        for (TeamCityBuildType buildType : buildTypes) {
            TeamCityBuild build = findBuildInBuildType(buildNumber, buildType);
            if (build != null) {
                return build;
            }
        }
        throw new TeamCityBuildNotFoundException("Can't find build #" + buildNumber + "of software project id "
                + projectId);
    }

    private TeamCityBuild findBuildInBuildType(String buildNumber, TeamCityBuildType buildType)
            throws TeamCityBuildNotFoundException {
        try {
            String buildTypeId = buildType.getId();
            TeamCityBuilds buildList = findBuildList(buildTypeId);
            List<TeamCityBuildItem> builds = buildList.getBuilds();
            for (TeamCityBuildItem buildItem : builds) {
                if (buildItem.getNumber().equals(buildNumber)) {
                    String buildId = buildItem.getId();
                    int buildIdAsString = Integer.parseInt(buildId);
                    return findBuild(buildIdAsString);
                }
            }
        } catch (TeamCityBuildListNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        return null;
    }

    public List<TeamCityChange> findChanges(int buildId) throws TeamCityChangesNotFoundException {
        checkBuildId(buildId);
        List<TeamCityChange> changesList = new ArrayList<TeamCityChange>();
        TeamCityChanges changes;
        try {
            String changesUrl = urlBuilder.getChanges(buildId);
            changes = client.resource(changesUrl, TeamCityChanges.class);
        } catch (ResourceNotFoundException e) {
            throw new TeamCityChangesNotFoundException("Changes of build " + buildId + " has not been found", e);
        }
        for (TeamCityChange changeItem : changes.getChanges()) {
            String changeUrl = urlBuilder.getChange(changeItem.getId());
            try {
                TeamCityChange change = client.resource(changeUrl, TeamCityChange.class);
                changesList.add(change);
            } catch (ResourceNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't get change at " + changeUrl, e);
                }
            }
        }
        return changesList;
    }

    private void checkProjectId(String projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
    }

    private void checkBuildId(int buildId) {
        Preconditions.checkArgument(buildId >= 0, "buildId must be >= 0");
    }

}
