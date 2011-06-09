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

package net.awired.visuwall.core.service;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.plugin.jenkins.JenkinsPlugin;

import org.junit.Test;

public class ProjectServiceIT {

	@Test
	public void github_issue_35() throws BuildNotFoundException, ProjectNotFoundException {
		String jenkinsUrl = "http://ci.awired.net/jenkins/";
		JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
		ConnectionPlugin connection = jenkinsPlugin.getConnection(jenkinsUrl, null);
		int buildNumber = 29;
		ProjectId projectId = new ProjectId("Acml");
		projectId.setArtifactId("net.awired.aclm:aclm");
		projectId.addId("JENKINS_ID", "Acml");
		connection.findBuildByBuildNumber(projectId, buildNumber);
	}

}
