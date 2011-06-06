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

import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityProjects;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TeamCityJerseyClient {

    private Client client;

    public TeamCityJerseyClient(Client client) {
        this.client = client;
    }

    public List<TeamCityProject> getProjects() {
        WebResource resource = client.resource("url");
        TeamCityProjects teamCityProjects = resource.get(TeamCityProjects.class);
		return teamCityProjects.getProjects();
    }

    public TeamCityProject getProject(String projectId) {
        WebResource resource = client.resource("url");
        TeamCityProject teamCityProject = resource.get(TeamCityProject.class);
        return teamCityProject;
    }

	public TeamCityBuild getBuild(String projectId, int buildNumber) {
		WebResource resource = client.resource("url");
		TeamCityBuild teamCityBuild = resource.get(TeamCityBuild.class);
		return teamCityBuild;
	}

}
