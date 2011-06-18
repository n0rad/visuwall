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


public class JenkinsViewCapabilityIT implements ViewCapabilityTCK {

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
		List<String> projects = jenkins.findProjectsByView("struts");
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
		List<ProjectId> projectIds = jenkins.findProjectsByNames(names);
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
		List<ProjectId> projects = jenkins.findProjectsByViews(views);
		assertEquals(5, projects.size());
	}

}
