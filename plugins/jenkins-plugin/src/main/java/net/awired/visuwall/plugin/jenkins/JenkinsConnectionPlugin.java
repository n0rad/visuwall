/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.plugin.jenkins;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public final class JenkinsConnectionPlugin implements BuildConnectionPlugin {

	private static final Logger LOG = LoggerFactory.getLogger(JenkinsConnectionPlugin.class);

	@VisibleForTesting
	static final String JENKINS_ID = "JENKINS_ID";

	private Hudson hudson;

	private ProjectBuilder projectCreator = new ProjectBuilder();

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
		Preconditions.checkState(connected, "You must connect your plugin");

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
		Project project = projectCreator.buildProjectFrom(hudsonProject);
		ProjectId projectId = new ProjectId();
		projectId.setName(project.getName());
		projectId.addId(JENKINS_ID, project.getName());
		projectId.setArtifactId(hudsonProject.getArtifactId());
		return projectId;
	}

	@Override
	public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
			HudsonProject hudsonProject = hudson.findProject(projectName);
			Project project = projectCreator.buildProjectFrom(hudsonProject);
			State state = getState(projectId);
			project.setState(state);
			project.addId(JENKINS_ID, projectName);
			return project;
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public void populate(Project project) throws ProjectNotFoundException {
		Preconditions.checkNotNull(project, "project is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			HudsonProject hudsonProject = hudson.findProject(project.getName());
			projectCreator.addCurrentAndCompletedBuilds(project, hudsonProject);
			if (project.getCompletedBuild() != null) {
				project.setState(project.getCompletedBuild().getState());
			}
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
			return hudson.getEstimatedFinishTime(projectName);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
			return hudson.isBuilding(projectName);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@Override
	public State getState(ProjectId projectId) throws ProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
			String state = hudson.getState(projectName);
			return State.getStateByName(state);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	private String extractProjectNameFrom(ProjectId projectId) {
		return projectId.getId(JENKINS_ID);
	}

	@Override
	public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
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
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		Preconditions.checkState(connected, "You must connect your plugin");

		try {
			String projectName = extractProjectNameFrom(projectId);
			HudsonBuild build = hudson.findBuild(projectName, buildNumber);
			return projectCreator.buildBuildFrom(build);
		} catch (HudsonBuildNotFoundException e) {
			throw new BuildNotFoundException(e);
		} catch (HudsonProjectNotFoundException e) {
			throw new ProjectNotFoundException(e);
		}
	}

	@VisibleForTesting
	void setHudson(Hudson hudson) {
		this.hudson = hudson;
	}

	@Override
	public List<String> findProjectNames() {
		return hudson.findProjectNames();
	}

}
