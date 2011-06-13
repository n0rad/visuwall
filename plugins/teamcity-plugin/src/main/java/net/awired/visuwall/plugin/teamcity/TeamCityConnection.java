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
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class TeamCityConnection implements BuildCapability {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityConnection.class);

    @VisibleForTesting
    static final String TEAMCITY_ID = "TEAMCITY_ID";

    private boolean connected;

    TeamCity teamCity;

    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        connected = true;
    }

    @Override
    public boolean contains(ProjectId projectId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<ProjectId> findProjectsByNames(List<String> names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public State getState(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        return teamCity.findProjectNames();
    }

    @Override
    public List<ProjectId> findAllProjects() {
        checkConnected();
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        List<TeamCityProject> teamCityProjects = teamCity.findAllProjects();
        for (TeamCityProject teamCityProject : teamCityProjects) {
            ProjectId projectId = new ProjectId(teamCityProject.getName());
            projectId.addId(TEAMCITY_ID, teamCityProject.getId());
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        String id = projectId.getId(TEAMCITY_ID);
        TeamCityProject project = teamCity.findProject(id);
        return createProject(project);
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
        // TODO Auto-generated method stub

    }
}
