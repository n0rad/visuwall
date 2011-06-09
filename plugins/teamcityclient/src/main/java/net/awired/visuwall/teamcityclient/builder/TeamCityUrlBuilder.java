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

package net.awired.visuwall.teamcityclient.builder;

import com.google.common.base.Preconditions;

public class TeamCityUrlBuilder {

	private String teamCityUrl;
	private static final String API_URI = "/app/rest";

	public TeamCityUrlBuilder(String teamCityUrl) {
		Preconditions.checkNotNull(teamCityUrl, "teamCityUrl is mandatory");
		this.teamCityUrl = teamCityUrl;
	}

	public String getProjects() {
		return build("/projects");
	}

	public String getProject(String projectId) {
		checkProjectId(projectId);
		return build("/projects/id:" + projectId);
	}

	public String getBuildType(String buildTypeId) {
		Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
		return build("/buildTypes/id:" + buildTypeId);
	}

	public String getBuild(int buildId) {
		Preconditions.checkArgument(buildId >= 0, "buildId must be >= 0");
		return build("/builds/id:" + buildId);
	}

	private String build(String url) {
		return teamCityUrl + API_URI + url;
	}

	public String getBuildList(int buildTypeId) {
		return build("/buildTypes/id:bt" + buildTypeId + "/builds");
	}

	private void checkProjectId(String projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
	}
}
