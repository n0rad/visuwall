package net.awired.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;

import org.junit.Before;
import org.junit.Test;


public class JenkinsConnectionIT {

	JenkinsConnection jenkinsConnection = new JenkinsConnection();

	@Before
	public void init() {
		jenkinsConnection.connect(IntegrationTestData.JENKINS_URL);
	}

	@Test
	public void should_list_all_views() {
		List<String> viewNames = jenkinsConnection.findViews();

		String[] views = { "in_error", "struts" };
		for (String viewName : views) {
			assertTrue(viewNames.contains(viewName));
		}
	}

	@Test
	public void should_list_all_project_in_a_view() throws ViewNotFoundException {
		List<String> projects = jenkinsConnection.findProjectsByView("struts");
		assertEquals(2, projects.size());

		String[] projectNames = { "struts", "struts 2 instable" };
		for (String projectName : projectNames) {
			assertTrue(projects.contains(projectName));
		}
	}

	@Test
	public void should_find_project_ids_by_names() {
		List<String> names = Arrays.asList("struts", "struts 2 instable");
		List<ProjectId> projectIds = jenkinsConnection.findProjectsByNames(names);
		ProjectId struts = projectIds.get(0);
		ProjectId struts2instable = projectIds.get(1);

		assertEquals(2, projectIds.size());
		assertEquals("org.apache.struts:struts-parent", struts.getArtifactId());
		assertEquals("org.apache.struts:struts2-parent", struts2instable.getArtifactId());
	}

	@Test
	public void should_find_all_projects_of_views() {
		List<String> views = Arrays.asList("in_error", "struts");
		List<ProjectId> projects = jenkinsConnection.findProjectsByViews(views);
		assertEquals(5, projects.size());
	}

	@Test
	public void should_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		assertTrue(jenkinsConnection.contains(projectId));
	}

	@Test
	public void should_not_contain_project() {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts3");
		assertFalse(jenkinsConnection.contains(projectId));
	}

	@Test
	public void should_find_all_project_names() {
		List<String> names = Arrays.asList("errorproject", "failproject", "freestyle-project", "itcoverage-project",
		        "neverbuild", "newproject", "struts", "struts 2 instable", "successproject", "test-change-result",
		        "disabled");
		List<String> projectNames = jenkinsConnection.findProjectNames();
		assertEquals(names.size(), projectNames.size());
		for (String name : names) {
			assertTrue(projectNames.contains(name));
		}
	}

	@Test
	public void should_find_build_by_build_number() throws BuildNotFoundException, ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Build build = jenkinsConnection.findBuildByBuildNumber(projectId, 3);
		assertNotNull(build);
		assertEquals(3, build.getBuildNumber());
		assertEquals(State.SUCCESS, build.getState());
	}

	@Test
	public void should_get_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		int number = jenkinsConnection.getLastBuildNumber(projectId);
		assertEquals(4, number);
	}

	@Test
	public void should_get_state() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		State state = jenkinsConnection.getState(projectId);
		assertEquals(State.SUCCESS, state);
	}

	@Test
	public void should_find_all_projects() {
		List<String> names = Arrays.asList("errorproject", "failproject", "freestyle-project", "itcoverage-project",
		        "neverbuild", "newproject", "struts", "struts 2 instable", "successproject", "test-change-result",
		        "disabled");
		List<ProjectId> projectNames = jenkinsConnection.findAllProjects();
		for (ProjectId projectId : projectNames) {
			assertTrue(names.contains(projectId.getName()));
		}
	}

	@Test
	public void should_find_a_project() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Project project = jenkinsConnection.findProject(projectId);
		assertNotNull(project);
	}

	@Test
	public void should_get_is_building() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		boolean isBuilding = jenkinsConnection.isBuilding(projectId);
		assertFalse(isBuilding);
	}

	@Test
	public void should_get_estimated_date() throws ProjectNotFoundException {
		ProjectId projectId = new ProjectId();
		projectId.addId(JenkinsConnection.JENKINS_ID, "struts");
		Date date = jenkinsConnection.getEstimatedFinishTime(projectId);
		assertNotNull(date);
	}
}
