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

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.teamcityclient.TeamCity;
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
    static final String TEAMCITY_ID = "TEAMCITY_ID";

    private boolean connected;

    TeamCity teamCity;

    private String url;

    @Override
    public void connect(String url, String login, String password) {
        connect(url);
    }

    void connect(String url) {
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
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("Not implemented");
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("Not implemented");
    }

    @Override
    public int[] getBuildNumbers(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        return new ArrayList<SoftwareProjectId>();
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
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
    public State getLastBuildState(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    private Project createProject(TeamCityProject teamCityProject) {
        Project project = new Project(teamCityProject.getName());
        project.setDescription(teamCityProject.getDescription());
        return project;
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

}
