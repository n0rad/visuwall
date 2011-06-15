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
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonViewNotFoundException;
import net.awired.visuwall.plugin.hudson.builder.BuildBuilder;
import net.awired.visuwall.plugin.hudson.builder.ProjectBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public final class HudsonConnection implements Connection, BuildCapability, ViewCapability {

	private static final Logger LOG = LoggerFactory.getLogger(HudsonConnection.class);

	private static final String HUDSON_ID = "HUDSON_ID";

	@VisibleForTesting
	Hudson hudson;

	private ProjectBuilder projectBuilder = new ProjectBuilder();
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
	public List<ProjectId> findAllProjects() {
		List<ProjectId> projectIds = new ArrayList<ProjectId>();
		for (HudsonProject hudsonProject : hudson.findAllProjects()) {
			try {
				ProjectId projectId = createProjectIdFrom(hudsonProject);
				projectIds.add(projectId);
			} catch (HudsonProjectNotFoundException e) {
				LOG.warn(e.getMessage(), e);
			}
		}
		return projectIds;
	}

	private ProjectId createProjectIdFrom(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
		Project project = projectBuilder.buildProjectFrom(hudsonProject);
		ProjectId projectId = new ProjectId();
		projectId.setName(project.getName());
		projectId.addId(HUDSON_ID, project.getName());
		projectId.setArtifactId(hudsonProject.getArtifactId());
		return projectId;
	}

	@Override
	public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			if (projectName == null) {
				throw new ProjectNotFoundException("Project " + projectId + " has no name");
			}
			HudsonProject hudsonProject = hudson.findProject(projectName);
			Project project = projectBuilder.buildProjectFrom(hudsonProject);
			State state = getState(projectId);
			project.setState(state);
			project.addId(HUDSON_ID, projectName);
			return project;
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			return hudson.getEstimatedFinishTime(projectName);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	private void checkProjectId(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
	}

	private void checkConnected() {
		Preconditions.checkState(connected, "You must connect your plugin");
	}

	@Override
	public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			if (projectName == null) {
				throw new ProjectNotFoundException("Project " + projectId + " has no name");
			}
			return hudson.isBuilding(projectName);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public State getState(ProjectId projectId) throws ProjectNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			if (projectName == null) {
				throw new ProjectNotFoundException("Project " + projectId + " has no name");
			}
			String state = hudson.getState(projectName);
			return State.getStateByName(state);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	private String extractProjectNameFrom(ProjectId projectId) {
		return projectId.getId(HUDSON_ID);
	}

	@Override
	public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			if (projectName == null) {
				throw new ProjectNotFoundException("Project " + projectId + " has no name");
			}
			return hudson.getLastBuildNumber(projectName);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		} catch (HudsonBuildNotFoundException e) {
			throw new BuildNotFoundException(e);
		}
	}

	@Override
	public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
	        ProjectNotFoundException {
		checkProjectId(projectId);
		checkConnected();
		try {
			String projectName = extractProjectNameFrom(projectId);
			if (projectName == null) {
				throw new BuildNotFoundException("Project " + projectId + " has no name");
			}
			HudsonBuild build = hudson.findBuild(projectName, buildNumber);
			return buildBuilder.createBuildFrom(build);
		} catch (HudsonBuildNotFoundException e) {
			throw new BuildNotFoundException(e);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public List<String> findProjectNames() {
		return hudson.findProjectNames();
	}

	@Override
	public List<String> findViews() {
		return hudson.findViews();
	}

	@Override
	public List<String> findProjectsByView(String viewName) throws ViewNotFoundException {
		Preconditions.checkNotNull(viewName, "viewName is mandatory");
		try {
			return hudson.findProjectNameByView(viewName);
		} catch (HudsonViewNotFoundException e) {
			throw new ViewNotFoundException("can't find view named :" + viewName, e);
		}
	}

	@Override
	public List<ProjectId> findProjectsByViews(List<String> views) {
		Set<ProjectId> projectIds = new HashSet<ProjectId>();
		for (String viewName : views) {
			try {
				List<String> projectNames = hudson.findProjectNameByView(viewName);
				projectIds.addAll(findProjectsByNames(projectNames));
			} catch (HudsonViewNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage(), e);
				}
			}
		}
		return new ArrayList<ProjectId>(projectIds);
	}

	@Override
	public boolean contains(ProjectId projectId) {
		try {
			String name = projectId.getId(HUDSON_ID);
			hudson.findProject(name);
		} catch (HudsonProjectNotFoundException e) {
			return false;
		}
		return true;
	}

	@Override
	public List<ProjectId> findProjectsByNames(List<String> names) {
		List<ProjectId> projectIds = new ArrayList<ProjectId>();
		for (String name : names) {
			try {
				HudsonProject project = hudson.findProject(name);
				String projectName = project.getName();
				ProjectId projectId = new ProjectId(projectName);
				projectId.addId(HUDSON_ID, projectName);
			} catch (HudsonProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage(), e);
				}
			}
		}
		return projectIds;
	}

	@Override
	public void close() {
	}

}
