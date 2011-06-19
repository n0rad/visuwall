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
import java.util.Date;
import java.util.List;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class TeamCityConnection implements Connection, BuildCapability {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityConnection.class);

    @VisibleForTesting
    public static final String TEAMCITY_ID = "TEAMCITY_ID";

    private boolean connected;

    TeamCity teamCity;

    private String url;

    private ProjectNotFoundException projectNotFoundException = new ProjectNotFoundException("No project found");

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
    public boolean contains(ProjectId projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
        checkConnected();
        try {
            String id = projectId.getId(TEAMCITY_ID);
            if (id == null) {
                return false;
            }
            teamCity.findProject(id);
            return true;
        } catch (TeamCityProjectNotFoundException e) {
            return false;
        }
    }

    @Override
    public List<ProjectId> findProjectIdsByNames(List<String> names) {
        checkConnected();
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        try {
            List<TeamCityProject> projects = teamCity.findAllProjects();
            for (TeamCityProject project : projects) {
                String name = project.getName();
                if (names.contains(name)) {
                    String id = project.getId();
                    ProjectId projectId = new ProjectId(name);
                    projectId.addId(TEAMCITY_ID, id);
                    projectIds.add(projectId);
                }
            }
        } catch (TeamCityProjectsNotFoundException e) {
            LOG.warn("Can't find projects by name with this Team City connection," + this.url, e);
        }
        return projectIds;
    }

    @Override
    public State getLastBuildState(ProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("No project found");
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        checkConnected();
        throw new BuildNotFoundException("No build found");
    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        throw projectNotFoundException;
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        throw projectNotFoundException;
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNumberNotFoundException {
        checkConnected();
        throw projectNotFoundException;
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
    public List<ProjectId> findAllProjects() {
        checkConnected();
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        try {
            List<TeamCityProject> teamCityProjects = teamCity.findAllProjects();
            for (TeamCityProject teamCityProject : teamCityProjects) {
                ProjectId projectId = new ProjectId(teamCityProject.getName());
                projectId.addId(TEAMCITY_ID, teamCityProject.getId());
                projectIds.add(projectId);
            }
        } catch (TeamCityProjectsNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return projectIds;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        String id = projectId.getId(TEAMCITY_ID);
        try {
            TeamCityProject project = teamCity.findProject(id);
            return createProject(project);
        } catch (TeamCityProjectNotFoundException e) {
            throw new ProjectNotFoundException("No project found with projectId:" + projectId, e);
        }
    }

    private Project createProject(TeamCityProject teamCityProject) {
        Project project = new Project(teamCityProject.getName());
        project.setDescription(teamCityProject.getDescription());
        return project;
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    @Override
    public void close() {
    }
}
