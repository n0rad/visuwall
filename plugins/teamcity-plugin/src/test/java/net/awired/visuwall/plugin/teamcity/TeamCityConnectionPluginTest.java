package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.teamcityclient.TeamCity;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildItem;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityStatus;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class TeamCityConnectionPluginTest {

	private TeamCity teamCity;
	private TeamCityConnectionPlugin connectionPlugin;

	@Before
	public void init() {
		teamCity = Mockito.mock(TeamCity.class);
		connectionPlugin = createConnectionPlugin();
	}

	@Test(expected = NullPointerException.class)
	public void should_fail_when_passing_null() {
		connectionPlugin.connect(null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void should_fail_when_passing_empty_string() {
		connectionPlugin.connect("");
	}

	@Test
	public void should_call_find_project_names() {
		connectionPlugin.findProjectNames();
		verify(teamCity).findProjectNames();
	}

	@Test
	public void should_build_one_project_id() {
		List<TeamCityProject> teamCityProjects = new ArrayList<TeamCityProject>();
		TeamCityProject teamCityProject = new TeamCityProject();
		teamCityProject.setName("projectName");
		teamCityProject.setId("projectId");
		teamCityProjects.add(teamCityProject);
		
		when(teamCity.findAllProjects()).thenReturn(teamCityProjects );
		
		List<ProjectId> projects = connectionPlugin.findAllProjects();
		ProjectId projectId = projects.get(0);
		
		assertEquals("projectName", projectId.getName());
		assertEquals("projectId", projectId.getId(TeamCityConnectionPlugin.TEAMCITY_ID));
	}

	@Test
	public void should_find_project() throws ProjectNotFoundException {
		TeamCityProject teamCityProject = new TeamCityProject();
		teamCityProject.setName("projectName");
		teamCityProject.setDescription("description");

		when(teamCity.findProject("projectId")).thenReturn(teamCityProject);

		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnectionPlugin.TEAMCITY_ID, "projectId");
		Project project = connectionPlugin.findProject(projectId);

		assertEquals("projectName", project.getName());
		assertEquals("description", project.getDescription());
		// assertEquals(State.SUCCESS, project.getState());
	}

	@Ignore
	@Test
	public void should_find_state_project() throws ProjectNotFoundException {
		TeamCityBuildItem buildItem = new TeamCityBuildItem();
		buildItem.setStatus(TeamCityStatus.SUCCESS);

		List<TeamCityBuildItem> buildItems = new ArrayList<TeamCityBuildItem>();
		buildItems.add(buildItem);

		TeamCityBuilds builds = new TeamCityBuilds();
		builds.setBuilds(buildItems);

		TeamCityProject teamCityProject = new TeamCityProject();
		when(teamCity.findProject("projectId")).thenReturn(teamCityProject);
		when(teamCity.findBuildList("bt297"));

		ProjectId projectId = new ProjectId();
		projectId.addId(TeamCityConnectionPlugin.TEAMCITY_ID, "projectId");
		Project project = connectionPlugin.findProject(projectId);

		assertEquals(State.SUCCESS, project.getState());
	}

	private TeamCityConnectionPlugin createConnectionPlugin() {
	    TeamCityConnectionPlugin connectionPlugin = new TeamCityConnectionPlugin();
		connectionPlugin.connect("http://");
		connectionPlugin.teamCity = teamCity;
	    return connectionPlugin;
    }
}
