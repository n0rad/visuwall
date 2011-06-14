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

import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.exception.ResourceNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildListNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityProjects;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class TeamCityJerseyClient {

	private Client client;

	private TeamCityUrlBuilder urlBuilder;

	public TeamCityJerseyClient(Client client, TeamCityUrlBuilder urlBuilder) {
		this.client = client;
		this.urlBuilder = urlBuilder;
	}

	public List<TeamCityProject> getProjects() throws TeamCityProjectsNotFoundException {
		try {
			String projectsUrl = urlBuilder.getProjects();
			WebResource resource = resource(projectsUrl);
			TeamCityProjects teamCityProjects = resource.get(TeamCityProjects.class);
			return teamCityProjects.getProjects();
		} catch (ResourceNotFoundException e) {
			throw new TeamCityProjectsNotFoundException("Projects have not been found", e);
		}
	}

	public TeamCityProject getProject(String projectId) throws TeamCityProjectNotFoundException {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		try {
			String projectUrl = urlBuilder.getProject(projectId);
			WebResource resource = resource(projectUrl);
			TeamCityProject teamCityProject = resource.get(TeamCityProject.class);
			return teamCityProject;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityProjectNotFoundException("Project #" + projectId + " has not been found", e);
		}
	}

	public TeamCityBuild getBuild(int buildNumber) throws TeamCityBuildNotFoundException {
		Preconditions.checkArgument(buildNumber >= 0, "buildNumber must be >= 0");
		try {
			String buildUrl = urlBuilder.getBuild(buildNumber);
			WebResource resource = resource(buildUrl);
			TeamCityBuild teamCityBuild = resource.get(TeamCityBuild.class);
			return teamCityBuild;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityBuildNotFoundException("Build #" + buildNumber + " has not been found", e);
		}
	}

	public TeamCityBuilds getBuildList(String buildTypeId) throws TeamCityBuildListNotFoundException {
		Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
		try {
			String buildListUrl = urlBuilder.getBuildList(buildTypeId);
			WebResource resource = resource(buildListUrl);
			TeamCityBuilds teamCityBuilds = resource.get(TeamCityBuilds.class);
			return teamCityBuilds;
		} catch (ResourceNotFoundException e) {
			throw new TeamCityBuildListNotFoundException("Build list of buildTypeId " + buildTypeId
			        + " has not been found", e);
		}
	}

	private WebResource resource(String url) throws ResourceNotFoundException {
		try {
			return client.resource(url);
		} catch (UniformInterfaceException e) {
			throw new ResourceNotFoundException(e);
		}
	}

}
