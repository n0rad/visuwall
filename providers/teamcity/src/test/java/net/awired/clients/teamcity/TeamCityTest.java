package net.awired.clients.teamcity;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.Maven;
import net.awired.clients.teamcity.exception.TeamCityBuildNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityBuildItem;
import net.awired.clients.teamcity.resource.TeamCityBuildType;
import net.awired.clients.teamcity.resource.TeamCityBuilds;
import net.awired.clients.teamcity.resource.TeamCityProject;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class TeamCityTest {

    @InjectMocks
    TeamCity teamCity = new TeamCity();

    @Mock
    GenericSoftwareClient client;

    @Mock
    TeamCityUrlBuilder urlBuilder;

    @Mock
    Maven maven;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_parameter_in_constructor() {
        new TeamCity(null);
    }

    @Test(expected = IllegalArgumentException.class)
    public void cant_pass_negative_number_to_find_build_method() throws TeamCityBuildNotFoundException {
        TeamCity teamcity = new TeamCity();
        teamcity.findBuild(-1);
    }

    @Test
    public void should_extract_maven_id_from_pom() throws Exception {
        TeamCityProject project = new TeamCityProject();
        TeamCityBuildType buildType = new TeamCityBuildType();
        buildType.setId("id");
        project.getBuildTypes().add(buildType);
        when(client.resource(anyString(), eq(TeamCityProject.class))).thenReturn(project);

        TeamCityBuilds builds = new TeamCityBuilds();
        TeamCityBuildItem build = new TeamCityBuildItem();
        build.setId("5");
        builds.getBuilds().add(build);
        when(client.resource(anyString(), eq(TeamCityBuilds.class))).thenReturn(builds);

        when(urlBuilder.getPomUrl(5)).thenReturn("pomUrl");
        when(maven.findMavenIdFrom("pomUrl")).thenReturn("net.awired.clients:clients-teamcity");

        String mavenId = teamCity.findMavenId("projectId");

        assertEquals("net.awired.clients:clients-teamcity", mavenId);
    }
}
