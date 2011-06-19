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

package net.awired.visuwall.plugin.teamcity.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class TeamBasicCapabilityITest implements BasicCapabilityTCK {

	BuildCapability teamcity = new TeamCityConnection();

	@Before
    public void init() throws ConnectionException {
		((Connection) teamcity).connect(IntegrationTestData.TEAMCITY_URL, "guest", "");
	}

	@Override
	@Test
	public void should_find_all_projects_ids() {
		List<ProjectId> projectIds = teamcity.findAllProjects();

		assertFalse(projectIds.isEmpty());
		for (ProjectId projectId : projectIds) {
			String name = projectId.getName();
			assertFalse(name.isEmpty());
		}
	}

	@Override
	@Test
	public void should_find_project_ids_by_names() {
		List<String> names = Arrays.asList("Apache Ant", "Gradle");
		List<ProjectId> projectIds = teamcity.findProjectIdsByNames(names);

		assertFalse(projectIds.isEmpty());

		ProjectId apacheAntId = projectIds.get(0);
		assertEquals("Apache Ant", apacheAntId.getName());

		ProjectId gradleId = projectIds.get(1);
		assertEquals("Gradle", gradleId.getName());
	}

	@Override
	@Test
	public void should_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnection.TEAMCITY_ID, "project33");

		boolean contains = teamcity.contains(projectId);

		assertTrue(contains);
	}

	@Override
	@Test
	public void should_not_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnection.TEAMCITY_ID, "does.not.exist");

		boolean contains = teamcity.contains(projectId);

		assertFalse(contains);
	}

	@Override
	@Test
	public void should_find_all_project_names() {
		List<String> names = Arrays.asList("Apache Ant", "Apache Ivy", "Gradle");

		List<String> projectNames = teamcity.findProjectNames();

		for (String name : names) {
			assertTrue(projectNames.contains(name));
		}
	}

	@Override
	@Test
	public void should_find_a_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnection.TEAMCITY_ID, "project33");

		Project project = teamcity.findProject(projectId);
		
		assertEquals("JGit", project.getName());
	}

	@Override
	@Ignore
	@Test
	public void should_get_disable_project() throws ProjectNotFoundException {
	}

}
