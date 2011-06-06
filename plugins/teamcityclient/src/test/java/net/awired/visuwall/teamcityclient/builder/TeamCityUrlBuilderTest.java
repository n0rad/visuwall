package net.awired.visuwall.teamcityclient.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TeamCityUrlBuilderTest {

	private static final String TEAM_CITY_URL = "http://teamcity.jetbrains.com";

	TeamCityUrlBuilder builder = new TeamCityUrlBuilder(TEAM_CITY_URL);

	@Test
	public void should_create_valid_projects_url() {
		String projectsUrl = builder.getProjects();
		assertEquals(TEAM_CITY_URL + "/app/rest/projects", projectsUrl);
	}

	@Test
	public void should_create_valid_project_url() {
		String projectUrl = builder.getProject("project54");
		assertEquals(TEAM_CITY_URL + "/app/rest/projects/id:project54", projectUrl);
	}

	@Test
	public void should_create_valid_build_url() {
		String buildTypeUrl = builder.getBuildType("bt296");
		assertEquals(TEAM_CITY_URL + "/app/rest/buildTypes/id:bt296", buildTypeUrl);
	}

}
