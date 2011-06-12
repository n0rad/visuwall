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

import java.util.Date;
import java.util.List;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildPlugin;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;

public class TeamCityConnectionPlugin implements BuildPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityConnectionPlugin.class);

    @VisibleForTesting
    static final String TEAMCITY_ID = "TEAMCITY_ID";

    private boolean connected;

    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        if (StringUtils.isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        connected = true;
    }

    @Override
    public boolean contains(ProjectId projectId) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<ProjectId> findAllProjects() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<ProjectId> findProjectsByNames(List<String> names) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> findProjectNames() {
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

}
