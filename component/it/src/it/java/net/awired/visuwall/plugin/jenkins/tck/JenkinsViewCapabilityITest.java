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

package net.awired.visuwall.plugin.jenkins.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.api.plugin.tck.ViewCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;

import org.junit.Before;
import org.junit.Test;


public class JenkinsViewCapabilityITest implements ViewCapabilityTCK {

	ViewCapability jenkins = new JenkinsConnection();

	@Before
    public void init() throws ConnectionException {
		((Connection) jenkins).connect(IntegrationTestData.JENKINS_URL, null, null);
	}

	@Override
	@Test
	public void should_list_all_views() {
		List<String> viewNames = jenkins.findViews();

		String[] views = { "in_error", "struts" };
		for (String viewName : views) {
			assertTrue(viewNames.contains(viewName));
		}
	}

	@Override
	@Test
	public void should_list_all_project_in_a_view() throws ViewNotFoundException {
		List<String> projects = jenkins.findProjectNamesByView("struts");
		assertEquals(2, projects.size());

		String[] projectNames = { "struts", "struts 2 instable" };
		for (String projectName : projectNames) {
			assertTrue(projects.contains(projectName));
		}
	}

	@Override
	@Test
	public void should_find_project_ids_by_names() {
		List<String> names = Arrays.asList("struts", "struts 2 instable");
		List<ProjectId> projectIds = jenkins.findProjectIdsByNames(names);
		ProjectId struts = projectIds.get(0);
		ProjectId struts2instable = projectIds.get(1);

		assertEquals(2, projectIds.size());
		assertEquals("org.apache.struts:struts-parent", struts.getArtifactId());
		assertEquals("org.apache.struts:struts2-parent", struts2instable.getArtifactId());
	}

	@Override
	@Test
	public void should_find_all_projects_of_views() {
		List<String> views = Arrays.asList("in_error", "struts");
		List<ProjectId> projects = jenkins.findProjectIdsByViews(views);
		assertEquals(5, projects.size());
	}

}
