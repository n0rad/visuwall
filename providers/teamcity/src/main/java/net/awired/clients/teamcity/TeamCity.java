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

package fr.norad.visuwall.providers.teamcity;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import javax.ws.rs.core.MediaType;

import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.Maven;
import fr.norad.visuwall.providers.common.MavenIdNotFoundException;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
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
import fr.norad.visuwall.providers.teamcity.resource.TeamCityChanges;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityProject;
import fr.norad.visuwall.providers.teamcity.resource.TeamCityProjects;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class TeamCity {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCity.class);

    private GenericSoftwareClient client;

    private TeamCityUrlBuilder urlBuilder;

    private Maven maven = new Maven();

    public TeamCity(String url, String login, String password) {
        checkNotNull(url, "url is mandatory");
        this.urlBuilder = new TeamCityUrlBuilder(url);
        this.client = new GenericSoftwareClient(login, password);
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
        checkNotNull(projectId, "projectId is mandatory");
        try {
            String projectUrl = urlBuilder.getProject(projectId);
            return client.resource(projectUrl, TeamCityProject.class);
        } catch (ResourceNotFoundException e) {
            throw new TeamCityProjectNotFoundException("Project #" + projectId + " has not been found", e);
        }
    }

    public TeamCityBuild findBuild(int buildId) throws TeamCityBuildNotFoundException {
        checkBuildId(buildId);
        checkArgument(buildId >= 0, "buildId must be >= 0");
        try {
            String buildUrl = urlBuilder.getBuild(buildId);
            TeamCityBuild teamCityBuild = client.resource(buildUrl, TeamCityBuild.class);
            return teamCityBuild;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildNotFoundException("Build #" + buildId + " has not been found", e);
        }
    }

    public TeamCityBuilds findBuildList(String buildTypeId) throws TeamCityBuildListNotFoundException {
        checkNotNull(buildTypeId, "buildTypeId is mandatory");
        try {
            String buildListUrl = urlBuilder.getBuildList(buildTypeId);
            TeamCityBuilds teamCityBuilds = client.resource(buildListUrl, TeamCityBuilds.class);
            return teamCityBuilds;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildListNotFoundException("Build list of buildTypeId " + buildTypeId
                    + " has not been found", e);
        }
    }

    public TeamCityBuild findBuild(String projectId, String buildId) throws TeamCityBuildNotFoundException,
            TeamCityBuildTypeNotFoundException {
        TeamCityBuildType buildType = findBuildType(projectId);
        TeamCityBuild build = findBuildInBuildType(buildId, buildType);
        if (build != null) {
            return build;
        }
        throw new TeamCityBuildNotFoundException("Can't find build #" + buildId + " of software project id "
                + projectId);
    }

    private TeamCityBuild findBuildInBuildType(String buildId, TeamCityBuildType buildType)
            throws TeamCityBuildNotFoundException {
        try {
            String buildTypeId = buildType.getId();
            TeamCityBuilds buildList = findBuildList(buildTypeId);
            List<TeamCityBuildItem> builds = buildList.getBuilds();
            for (TeamCityBuildItem buildItem : builds) {
                if (buildItem.getId().equals(buildId)) {
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

    public TeamCityBuild findRunningBuild() throws TeamCityBuildNotFoundException {
        String runningBuildUrl = urlBuilder.getRunningBuild();
        try {
            TeamCityBuild teamCityBuild = client.resource(runningBuildUrl, TeamCityBuild.class);
            return teamCityBuild;
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildNotFoundException("There is no running build", e);
        }
    }

    public String findMavenId(String projectId) throws MavenIdNotFoundException {
        try {
            LOG.info("Try to find Maven id of project: " + projectId);
            String id = findLastBuild(projectId).getId();
            int buildId = Integer.valueOf(id);
            String pomUrl = urlBuilder.getPomUrl(buildId);
            String pomContent = client.resource(pomUrl, String.class, MediaType.TEXT_PLAIN_TYPE);
            String mavenId = maven.findMavenIdFromContent(pomContent);
            LOG.info("Maven id of" + projectId + " is " + mavenId);
            return mavenId;
        } catch (TeamCityBuildNotFoundException e) {
            throw new MavenIdNotFoundException("Cannot find maven id for " + projectId, e);
        } catch (ResourceNotFoundException e) {
            throw new MavenIdNotFoundException("Cannot find maven id for " + projectId, e);
        }
    }

    public TeamCityAbstractBuild findLastBuild(String projectId) throws TeamCityBuildNotFoundException {
        try {
            TeamCityBuild runningBuild = findRunningBuild();
            if (runningBuild.getBuildType().getId().equals(projectId)) {
                return runningBuild;
            }
        } catch (TeamCityBuildNotFoundException ex) {
        }
        try {
            TeamCityBuildType buildType = findBuildType(projectId);
            TeamCityBuilds buildList = findBuildList(buildType.getId());
            if (!buildList.getBuilds().isEmpty()) {
                TeamCityBuildItem build = buildList.getBuilds().get(0);
                return build;
            }
            throw new TeamCityBuildNotFoundException("Cannot find last build of " + projectId);
        } catch (TeamCityBuildTypeNotFoundException e) {
            throw new TeamCityBuildNotFoundException("Cannot find last build of " + projectId, e);
        } catch (TeamCityBuildListNotFoundException e) {
            throw new TeamCityBuildNotFoundException("Cannot find last build of " + projectId, e);
        }
    }

    private void checkProjectId(String projectId) {
        checkNotNull(projectId, "projectId is mandatory");
    }

    private void checkBuildId(int buildId) {
        checkArgument(buildId >= 0, "buildId must be >= 0");
    }

    public TeamCityProject findProjectByName(String projectName) throws TeamCityProjectNotFoundException {
        try {
            List<TeamCityProject> projects = findAllProjects();
            for (TeamCityProject project : projects) {
                if (projectName.equals(project.getName())) {
                    return project;
                }
            }
        } catch (TeamCityProjectsNotFoundException e) {
            throw new TeamCityProjectNotFoundException("Cannot find project with name " + projectName, e);
        }
        throw new TeamCityProjectNotFoundException("Cannot find project with name " + projectName);
    }

    public TeamCityBuildType findBuildType(String buildTypeId) throws TeamCityBuildTypeNotFoundException {
        try {
            String buildTypeUrl = urlBuilder.getBuildType(buildTypeId);
            return client.resource(buildTypeUrl, TeamCityBuildType.class);
        } catch (ResourceNotFoundException e) {
            throw new TeamCityBuildTypeNotFoundException("Cannot find build type with id: " + buildTypeId, e);
        }
    }
}
