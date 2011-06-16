package net.awired.visuwall.plugin.jenkins.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.jenkins.JenkinsConnection;

import org.junit.Before;
import org.junit.Test;


public class JenkinsBasicCapabilityIT implements BasicCapabilityTCK {

	BasicCapability jenkins = new JenkinsConnection();

	@Before
	public void init() {
		((Connection) jenkins).connect(IntegrationTestData.JENKINS_URL, null, null);
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
	public void should_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		assertTrue(jenkins.contains(projectId));
	}

	@Override
	@Test
	public void should_not_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts3");
		assertFalse(jenkins.contains(projectId));
	}

	@Override
	@Test
	public void should_find_all_project_names() {
		List<String> names = Arrays.asList("errorproject", "failproject", "freestyle-project", "itcoverage-project",
		        "neverbuild", "newproject", "struts", "struts 2 instable", "successproject", "test-change-result",
		        "disabled");
		List<String> projectNames = jenkins.findProjectNames();
		assertEquals(names.size(), projectNames.size());
		for (String name : names) {
			assertTrue(projectNames.contains(name));
		}
	}

	@Override
	@Test
	public void should_find_all_projects_ids() {
		List<String> names = Arrays.asList("errorproject", "failproject", "freestyle-project", "itcoverage-project",
		        "neverbuild", "newproject", "struts", "struts 2 instable", "successproject", "test-change-result",
		        "disabled");
		List<ProjectId> projectNames = jenkins.findAllProjects();
		for (ProjectId projectId : projectNames) {
			assertTrue(names.contains(projectId.getName()));
		}
	}

	@Override
	@Test
	public void should_find_a_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Project project = jenkins.findProject(projectId);
		assertNotNull(project);
	}

}
