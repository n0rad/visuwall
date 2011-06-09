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

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class TeamCity {

	@VisibleForTesting
	TeamCityJerseyClient teamcityJerseyClient;

	public List<String> findProjectNames() {
		List<String> projectNames = new ArrayList<String>();
		List<TeamCityProject> projects = teamcityJerseyClient.getProjects();
		for (TeamCityProject project : projects) {
			projectNames.add(project.getName());
		}
		return projectNames;
	}

	public List<TeamCityProject> findAllProjects() {
		return teamcityJerseyClient.getProjects();
	}

	public TeamCityProject findProject(String projectId) {
		checkProjectId(projectId);
		return teamcityJerseyClient.getProject(projectId);
	}

	public TeamCityBuild findBuild(int buildNumber) {
		Preconditions.checkArgument(buildNumber >= 0, "buildNumber must be >= 0");
		return teamcityJerseyClient.getBuild(buildNumber);
	}

	private void checkProjectId(String projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
	}

}
