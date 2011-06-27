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

package net.awired.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooBuildNumberNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooStateNotFoundException;
import org.apache.commons.lang.StringUtils;
import com.google.common.base.Preconditions;

public class BambooConnection implements BuildCapability {

    public static final String BAMBOO_ID = "BAMBOO_ID";

    private Bamboo bamboo;

    private boolean connected;

    @Override
    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        bamboo = new Bamboo(url);
        connected = true;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, Integer buildNumber) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = getProjectKey(projectId);
            BambooProject bambooProject = bamboo.findProject(projectName);
            return bambooProject.isBuilding();
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with ProjectId:" + projectId, e);
        }
    }

    @Override
    public State getBuildState(SoftwareProjectId projectId, Integer buildNumber) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = getProjectKey(projectId);
            String bambooState = bamboo.getState(projectName);
            return States.asVisuwallState(bambooState);
        } catch (BambooStateNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        String id = getProjectKey(projectId);
        Preconditions.checkNotNull(id, BAMBOO_ID);
        try {
            return bamboo.getLastBuildNumber(id);
        } catch (BambooBuildNumberNotFoundException e) {
            throw new BuildNumberNotFoundException(e);
        }
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        List<String> projectNames = new ArrayList<String>();
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            projectNames.add(project.getName());
        }
        return projectNames;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            String name = project.getName();
            if (names.contains(name)) {
                SoftwareProjectId projectId = new SoftwareProjectId(project.getKey());
                projectIds.add(projectId);
            }
        }
        return projectIds;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkBuildNumber(buildNumber);
        String projectName = getProjectKey(projectId);
        try {
            return bamboo.getEstimatedFinishTime(projectName);
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    private String getProjectKey(SoftwareProjectId projectId) {
        return projectId.getProjectId();
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<BambooProject> projects = bamboo.findAllProjects();
        for (BambooProject project : projects) {
            String key = project.getKey();
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
            projectIds.add(softwareProjectId);
        }
        return projectIds;
    }

    @Override
    public List<Integer> getBuildNumbers(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        return new ArrayList<Integer>();
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectId = softwareProjectId.getProjectId();
            BambooProject project = bamboo.findProject(projectId);
            return project.getName();
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id: " + softwareProjectId);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectKey = softwareProjectId.getProjectId();
            BambooProject project = bamboo.findProject(projectKey);
            String name = project.getName();
            return name;
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of software project id: " + softwareProjectId);
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
            String projectKey = softwareProjectId.getProjectId();
            BambooBuild bambooBuild = bamboo.findBuild(projectKey, buildNumber);
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(bambooBuild.getDuration());
            buildTime.setStartTime(bambooBuild.getStartTime());
            return buildTime;
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " of project " + softwareProjectId,
                    e);
        }
    }

    private void checkBuildNumber(Integer buildNumber) {
        Preconditions.checkNotNull(buildNumber, "buildNumber is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

}
