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

package net.awired.visuwall.plugin.hudson;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.ArtifactIdNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonViewNotFoundException;
import net.awired.visuwall.plugin.hudson.builder.BuildBuilder;
import net.awired.visuwall.plugin.hudson.builder.ProjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class HudsonConnection implements Connection, BuildCapability, ViewCapability {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonConnection.class);

    public static final String HUDSON_ID = "HUDSON_ID";

    @VisibleForTesting
    Hudson hudson;

    private ProjectBuilder projectCreator = new ProjectBuilder();
    private BuildBuilder buildBuilder = new BuildBuilder();

    private boolean connected;

    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        hudson = new Hudson(url);
        connected = true;
    }

    @Override
    @Deprecated
    public List<ProjectId> findAllProjects() {
        checkConnected();
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        List<HudsonProject> projects = hudson.findAllProjects();
        for (int i = 0; i < projects.size(); i++) {
            HudsonProject hudsonProject = projects.get(i);
            ProjectId projectId = createProjectIdFrom(hudsonProject);
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<HudsonProject> projects = hudson.findAllProjects();
        for (int i = 0; i < projects.size(); i++) {
            HudsonProject hudsonProject = projects.get(i);
            SoftwareProjectId projectId = new SoftwareProjectId(hudsonProject.getName());
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    @Deprecated
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        checkProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            if (projectName == null) {
                throw new ProjectNotFoundException("Project " + projectId + " has no name");
            }
            HudsonProject hudsonProject = hudson.findProject(projectName);
            Project project = projectCreator.buildProjectFrom(hudsonProject);
            project.addId(HUDSON_ID, projectName);
            return project;
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    @Deprecated
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        return getEstimatedFinishTime(new SoftwareProjectId(projectId.getId(HUDSON_ID)));
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            return hudson.getEstimatedFinishTime(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    @Deprecated
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        return isBuilding(new SoftwareProjectId(projectId.getId(HUDSON_ID)));
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            return hudson.isBuilding(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    @Deprecated
    public State getLastBuildState(ProjectId projectId) throws ProjectNotFoundException {
        return getLastBuildState(new SoftwareProjectId(projectId.getId(HUDSON_ID)));
    }

    @Override
    public State getLastBuildState(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            String hudsonState = hudson.getState(projectName);
            return States.asVisuwallState(hudsonState);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Deprecated
    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNumberNotFoundException {
        return getLastBuildNumber(new SoftwareProjectId(projectId.getId(HUDSON_ID)));
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkSoftwareProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            return hudson.getLastBuildNumber(projectName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNumberNotFoundException(e);
        }
    }

    @Override
    @Deprecated
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        checkProjectId(projectId);
        checkConnected();
        try {
            String projectName = jobName(projectId);
            HudsonBuild build = hudson.findBuild(projectName, buildNumber);
            return buildBuilder.createBuildFrom(build);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        return hudson.findProjectNames();
    }

    @Override
    public List<String> findViews() {
        checkConnected();
        return hudson.findViews();
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
        try {
            return hudson.findProjectNameByView(viewName);
        } catch (HudsonViewNotFoundException e) {
            throw new ViewNotFoundException("can't find view named :" + viewName, e);
        }
    }

    @Override
    @Deprecated
    public List<ProjectId> findProjectIdsByViews(List<String> views) {
        checkConnected();
        Preconditions.checkNotNull(views, "views is mandatory");
        Set<ProjectId> projectIds = new HashSet<ProjectId>();
        for (String viewName : views) {
            try {
                List<String> projectNames = hudson.findProjectNameByView(viewName);
                projectIds.addAll(findProjectIdsByNames(projectNames));
            } catch (HudsonViewNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<ProjectId>(projectIds);
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        checkConnected();
        Preconditions.checkNotNull(views, "views is mandatory");
        Set<SoftwareProjectId> projectIds = new HashSet<SoftwareProjectId>();
        for (String viewName : views) {
            try {
                List<String> projectNames = hudson.findProjectNameByView(viewName);
                projectIds.addAll(findSoftwareProjectIdsByNames(projectNames));
            } catch (HudsonViewNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return new ArrayList<SoftwareProjectId>(projectIds);
    }

    @Override
    @Deprecated
    public boolean contains(ProjectId projectId) {
        checkConnected();
        checkProjectId(projectId);
        try {
            String name = jobName(projectId);
            return hudson.contains(name);
        } catch (HudsonJobNotFoundException e) {
            return false;
        }
    }

    @Override
    @Deprecated
    public List<ProjectId> findProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<ProjectId> projectIds = new ArrayList<ProjectId>();
        for (String name : names) {
            try {
                ProjectId projectId = createProjectId(name);
                projectIds.add(projectId);
            } catch (ArtifactIdNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage(), e);
                }
            }
        }
        return projectIds;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        for (String name : names) {
            SoftwareProjectId projectId = new SoftwareProjectId(name);
            projectIds.add(projectId);
        }
        return projectIds;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String jobName = jobName(projectId);
            return hudson.getDescription(jobName);
        } catch (HudsonJobNotFoundException e) {
            throw new ProjectNotFoundException("Can't find description of project id: " + projectId, e);
        }
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        throw new ProjectNotFoundException("not implemented");
    }

    @Override
    public int[] getBuildNumbers(SoftwareProjectId projectId) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("not implemented");
    }

    private void checkProjectId(ProjectId projectId) {
        checkConnected();
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        checkConnected();
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    @Deprecated
    private String jobName(ProjectId projectId) throws HudsonJobNotFoundException {
        String jobName = projectId.getId(HUDSON_ID);
        if (jobName == null) {
            throw new HudsonJobNotFoundException("Project id " + projectId + " does not contain Hudson id");
        }
        return jobName;
    }

    private String jobName(SoftwareProjectId softwareProjectId) throws HudsonJobNotFoundException {
        String jobName = softwareProjectId.getProjectId();
        if (jobName == null) {
            throw new HudsonJobNotFoundException("Project id " + softwareProjectId + " does not contain Hudson id");
        }
        return jobName;
    }

    @Deprecated
    private ProjectId createProjectIdFrom(HudsonProject hudsonProject) {
        Project project = projectCreator.buildProjectFrom(hudsonProject);
        ProjectId projectId = new ProjectId();
        projectId.setName(project.getName());
        projectId.addId(HUDSON_ID, project.getName());
        projectId.setArtifactId(hudsonProject.getArtifactId());
        return projectId;
    }

    @Deprecated
    private ProjectId createProjectId(String projectName) throws ArtifactIdNotFoundException {
        String artifactId = hudson.findArtifactId(projectName);
        ProjectId projectId = new ProjectId();
        projectId.setName(projectName);
        projectId.addId(HUDSON_ID, projectName);
        projectId.setArtifactId(artifactId);
        return projectId;
    }

}
