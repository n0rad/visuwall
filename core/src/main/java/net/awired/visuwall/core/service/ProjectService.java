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

package net.awired.visuwall.core.service;

import java.util.Date;
import java.util.List;

import javax.persistence.Transient;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.core.domain.PluginHolder;
import net.awired.visuwall.core.domain.Wall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

@Service
public class ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

	@Autowired
	ProjectEnhancerService projectMergeService;

	private static final int PROJECT_NOT_BUILT_ID = -1;

	@Transient
	String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

	public void updateWallProjects(Wall wall) {
		Preconditions.checkNotNull(wall, "wall is a mandatory parameter");

		for (BuildConnectionPlugin buildService : wall.getPluginHolder().getBuildServices()) {
			List<ProjectId> discoveredProjects = buildService.findAllProjects();
			for (ProjectId discoveredProjectId : discoveredProjects) {

				Project project;
				try {
					project = wall.getProjectByProjectId(discoveredProjectId);
				} catch (ProjectNotFoundException e) {
					project = new Project(discoveredProjectId);
					wall.getProjects().add(project);
				}

				updateProject(wall.getPluginHolder(), project);
			}
		}
	}

	public void updateWallProject(Wall wall, String projectName) throws ProjectNotFoundException {
		Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
		Preconditions.checkNotNull(projectName, "projectName is a mandatory parameter");

		Project project = wall.getProjectByName(projectName);
		updateProject(wall.getPluginHolder(), project);
	}

	void updateProject(PluginHolder pluginHolder, Project project) {
		for (BuildConnectionPlugin service : pluginHolder.getBuildServices()) {
			projectMergeService.enhanceWithBuildInformations(project, service);
		}
		for (QualityConnectionPlugin service : pluginHolder.getQualityServices()) {
			projectMergeService.enhanceWithQualityAnalysis(project, service, metrics);
		}
		if (LOG.isDebugEnabled()) {
			LOG.debug(project.toString());
		}
	}

	/**
	 * @return null if no date could be estimated
	 * @throws ProjectNotFoundException
	 */
	public Date getEstimatedFinishTime(Wall wall, String projectName) throws ProjectNotFoundException {
		Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
		Preconditions.checkNotNull(projectName, "projectName is a mandatory parameter");

		ProjectId projectId = wall.getProjectByName(projectName).getProjectId();
		for (BuildConnectionPlugin service : wall.getPluginHolder().getBuildServices()) {
			try {
				Date estimatedFinishTime = service.getEstimatedFinishTime(projectId);
				if (estimatedFinishTime != null) {
					return estimatedFinishTime;
				}
			} catch (ProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		return null;
	}

	public int getLastBuildNumber(PluginHolder pluginHolder, ProjectId projectId) {
		Preconditions.checkNotNull(pluginHolder, "pluginHolder is a mandatory parameter");
		Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

		for (BuildConnectionPlugin service : pluginHolder.getBuildServices()) {
			try {
				return service.getLastBuildNumber(projectId);
			} catch (ProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			} catch (BuildNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		return PROJECT_NOT_BUILT_ID;
	}

	public State getState(PluginHolder pluginHolder, ProjectId projectId) {
		Preconditions.checkNotNull(pluginHolder, "pluginHolder is a mandatory parameter");
		Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

		for (BuildConnectionPlugin service : pluginHolder.getBuildServices()) {
			try {
				return service.getState(projectId);
			} catch (ProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		throw new RuntimeException("Project must have a state.");
	}

	public boolean isBuilding(PluginHolder pluginHolder, ProjectId projectId) {
		Preconditions.checkNotNull(pluginHolder, "pluginHolder is a mandatory parameter");
		Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

		for (BuildConnectionPlugin service : pluginHolder.getBuildServices()) {
			try {
				return service.isBuilding(projectId);
			} catch (ProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		return false;
	}

	public Build findBuildByBuildNumber(Wall wall, String projectName, int buildNumber) throws BuildNotFoundException,
	        ProjectNotFoundException {
		Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
		Preconditions.checkNotNull(projectName, "projectName is a mandatory parameter");

		ProjectId projectId = wall.getProjectByName(projectName).getProjectId();
		for (BuildConnectionPlugin service : wall.getPluginHolder().getBuildServices()) {
			try {
				Build build = service.findBuildByBuildNumber(projectId, buildNumber);
				if (build != null) {
					return build;
				}
			} catch (BuildNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			} catch (ProjectNotFoundException e) {
				if (LOG.isDebugEnabled()) {
					LOG.debug(e.getMessage());
				}
			}
		}
		throw new BuildNotFoundException("No build #" + buildNumber + " for project " + projectId);
	}

}
