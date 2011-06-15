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

package net.awired.visuwall.teamcityclient;

import java.util.List;

import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildListNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityProjects;

import com.google.common.base.Preconditions;

public class TeamCityFinder {

	private GenericSoftwareClient client;

	private TeamCityUrlBuilder urlBuilder;

	public TeamCityFinder(GenericSoftwareClient client, TeamCityUrlBuilder urlBuilder) {
		this.client = client;
		this.urlBuilder = urlBuilder;
	}

	public List<TeamCityProject> getProjects() throws TeamCityProjectsNotFoundException {
		try {
			String projectsUrl = urlBuilder.getProjects();
			TeamCityProjects teamCityProjects = client.resource(projectsUrl, TeamCityProjects.class);
			return teamCityProjects.getProjects();
		} catch (ResourceNotFoundException e) {
			throw new TeamCityProjectsNotFoundException("Projects have not been found", e);
		}
	}

	public TeamCityProject getProject(String projectId) throws TeamCityProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		try {
			String projectUrl = urlBuilder.getProject(projectId);
			TeamCityProject teamCityProject = client.resource(projectUrl, TeamCityProject.class);
			return teamCityProject;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityProjectNotFoundException("Project #" + projectId + " has not been found", e);
		}
	}

	public TeamCityBuild getBuild(int buildNumber) throws TeamCityBuildNotFoundException {
		Preconditions.checkArgument(buildNumber >= 0, "buildNumber must be >= 0");
		try {
			String buildUrl = urlBuilder.getBuild(buildNumber);
			TeamCityBuild teamCityBuild = client.resource(buildUrl, TeamCityBuild.class);
			return teamCityBuild;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityBuildNotFoundException("Build #" + buildNumber + " has not been found", e);
		}
	}

	public TeamCityBuilds getBuildList(String buildTypeId) throws TeamCityBuildListNotFoundException {
		Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
		try {
			String buildListUrl = urlBuilder.getBuildList(buildTypeId);
			TeamCityBuilds teamCityBuilds = client.resource(buildListUrl, TeamCityBuilds.class);
			return teamCityBuilds;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityBuildListNotFoundException("Build list of buildTypeId " + buildTypeId
			        + " has not been found", e);
		}
	}

}
