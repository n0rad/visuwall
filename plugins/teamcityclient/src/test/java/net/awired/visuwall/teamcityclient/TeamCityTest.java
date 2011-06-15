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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.exception.TeamCityBuildNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectNotFoundException;
import net.awired.visuwall.teamcityclient.exception.TeamCityProjectsNotFoundException;
import net.awired.visuwall.teamcityclient.resource.TeamCityAgent;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuild;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildItem;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuildType;
import net.awired.visuwall.teamcityclient.resource.TeamCityBuilds;
import net.awired.visuwall.teamcityclient.resource.TeamCityChanges;
import net.awired.visuwall.teamcityclient.resource.TeamCityProject;
import net.awired.visuwall.teamcityclient.resource.TeamCityProjects;
import net.awired.visuwall.teamcityclient.resource.TeamCityProperty;
import net.awired.visuwall.teamcityclient.resource.TeamCityRelatedIssue;
import net.awired.visuwall.teamcityclient.resource.TeamCityRevision;
import net.awired.visuwall.teamcityclient.resource.TeamCityTag;
import net.awired.visuwall.teamcityclient.resource.TeamCityVcsRoot;

import org.junit.Test;

import com.sun.jersey.api.client.WebResource;

public class TeamCityTest {

	@Test
	public void should_list_all_project_names() throws TeamCityProjectsNotFoundException {
		TeamCityProjects teamcityProjects = createProjects();
		TeamCityFinder teamcityJerseyClient = prepareClientFor(teamcityProjects);

		TeamCity teamcity = new TeamCity();
		teamcity.teamcityFinder = teamcityJerseyClient;

		List<String> projectNames = teamcity.findProjectNames();
		assertFalse(projectNames.isEmpty());
		for (String s : projectNames) {
			assertNotNull(s);
		}
	}

	@Test
	public void should_find_all_project() throws TeamCityProjectsNotFoundException {
		TeamCityProjects teamcityProjects = createProjects();
		TeamCityFinder teamcityJerseyClient = prepareClientFor(teamcityProjects);

		TeamCity teamcity = new TeamCity();
		teamcity.teamcityFinder = teamcityJerseyClient;

		List<TeamCityProject> projects = teamcity.findAllProjects();
		assertFalse(projects.isEmpty());
		for (TeamCityProject project : projects) {
			assertNotNull(project.getName());
			assertNotNull(project.getId());
			assertNotNull(project.getHref());
		}
	}

	@Test
	public void should_load_project() throws TeamCityProjectNotFoundException {
		TeamCityProject teamcityProject = createProject();
		TeamCityFinder teamcityJerseyClient = prepareClientFor(teamcityProject);

		TeamCity teamcity = new TeamCity();
		teamcity.teamcityFinder = teamcityJerseyClient;

		TeamCityProject project = teamcity.findProject("project54");
		assertEquals("http://teamcity.jetbrains.com/project.html?projectId=project54", project.getWebUrl());
		assertEquals("typica & maragogype", project.getDescription());
		assertFalse(project.isArchived());
		assertEquals("Amazon API client", project.getName());
		assertEquals("project54", project.getId());
		assertEquals("/app/rest/projects/id:project54", project.getHref());
	}

	@Test
	public void should_load_project_with_build_types() throws TeamCityProjectNotFoundException {
		TeamCityProject teamcityProject = createProject();
		TeamCityFinder teamcityJerseyClient = prepareClientFor(teamcityProject);

		TeamCity teamcity = new TeamCity();
		teamcity.teamcityFinder = teamcityJerseyClient;

		TeamCityProject project = teamcity.findProject("project54");

		List<TeamCityBuildType> teamCitybuildTypes = project.getBuildTypes();

		TeamCityBuildType bt297 = teamCitybuildTypes.get(0);

		assertEquals(3, teamCitybuildTypes.size());
		assertEquals("bt297", bt297.getId());
		assertEquals("Build", bt297.getName());
		assertEquals("/app/rest/buildTypes/id:bt297", bt297.getHref());
		assertEquals("Amazon API client", bt297.getProjectName());
		assertEquals("project54", bt297.getProjectId());
		assertEquals("http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt297", bt297.getWebUrl());
	}

	@Test
	public void should_load_build() throws TeamCityBuildNotFoundException {
		TeamCityBuild teamcityBuild = createBuild();

		TeamCity teamcity = new TeamCity();
		teamcity.teamcityFinder = prepareClientFor(teamcityBuild);

		TeamCityBuild build = teamcity.findBuild(47068);

		assertEquals("47068", build.getId());
		assertEquals("6", build.getNumber());
		assertEquals("SUCCESS", build.getStatus());
		assertEquals("/app/rest/builds/id:47068", build.getHref());
		assertEquals("http://teamcity.jetbrains.com/viewLog.html?buildId=47068&buildTypeId=bt297", build.getWebUrl());
		assertFalse(build.isPersonal());
		assertFalse(build.isHistory());
		assertFalse(build.isPinned());

		assertEquals("Success", build.getStatusText());

		TeamCityBuildType buildType = build.getBuildType();
		assertEquals("bt297", buildType.getId());
		assertEquals("Build", buildType.getName());
		assertEquals("/app/rest/buildTypes/id:bt297", buildType.getHref());
		assertEquals("Amazon API client", buildType.getProjectName());
		assertEquals("project54", buildType.getProjectId());
		assertEquals("http://teamcity.jetbrains.com/viewType.html?buildTypeId=bt297", buildType.getWebUrl());

		assertNotNull(build.getStartDate());
		assertNotNull(build.getFinishDate());

		TeamCityAgent agent = build.getAgent();
		assertEquals("win-6-m-i-b97d43d5", agent.getName());

		List<TeamCityTag> tags = build.getTags();
		assertTrue(tags.isEmpty());

		List<TeamCityProperty> properties = build.getProperties();
		TeamCityProperty property = properties.get(0);
		assertEquals("system.rel.version", property.getName());
		assertEquals("1.7.x", property.getValue());

		List<TeamCityRevision> revisions = build.getRevisions();
		TeamCityRevision revision = revisions.get(0);
		assertEquals(346, revision.getDisplayVersion());

		TeamCityVcsRoot vcsRoot = revision.getVcsRoot();
		assertEquals("/app/rest/vcs-roots/id:1084", vcsRoot.getHref());
		assertEquals("http://typica.googlecode.com/svn/trunk", vcsRoot.getName());

		TeamCityChanges changes = build.getChanges();
		assertEquals("/app/rest/changes?build=id:47068", changes.getHref());
		assertEquals(0, changes.getCount());

		List<TeamCityRelatedIssue> relatedIssues = build.getRelatedIssues();
		assertTrue(relatedIssues.isEmpty());
	}

	@Test
	public void should_load_build_type_with_builds() {
		TeamCityBuilds builds = createBuilds();

		assertEquals("/app/rest/builds?count=100&start=100", builds.getNextHref());
		assertEquals(100, builds.getCount());

		List<TeamCityBuildItem> buildList = builds.getBuilds();
		assertEquals(100, buildList.size());

		TeamCityBuildItem build = buildList.get(0);
		assertEquals("51753", build.getId());
		assertEquals("421", build.getNumber());
		assertEquals("ERROR", build.getStatus());
		assertEquals("bt213", build.getBuildTypeId());
		assertNotNull(build.getStartDate());
		assertEquals("/app/rest/builds/id:51753", build.getHref());
		assertEquals("http://teamcity.jetbrains.com/viewLog.html?buildId=51753&buildTypeId=bt213", build.getWebUrl());
	}

	@SuppressWarnings("unchecked")
	private TeamCityFinder prepareClientFor(Object o) {
		WebResource resource = mock(WebResource.class);
		when(resource.get(any(Class.class))).thenReturn(o);

		GenericSoftwareClient client = mock(GenericSoftwareClient.class);
		try {
			when(client.resource(anyString(), any(Class.class))).thenReturn(o);
		} catch (ResourceNotFoundException e) {
			throw new RuntimeException(e);
		}

		TeamCityUrlBuilder teamCityUrlBuilder = mock(TeamCityUrlBuilder.class);

		return new TeamCityFinder(client, teamCityUrlBuilder);
	}

	private TeamCityBuild createBuild() {
		return (TeamCityBuild) load("app/rest/builds/id:47068.xml", TeamCityBuild.class);
	}

	private TeamCityProjects createProjects() {
		return (TeamCityProjects) load("app/rest/projects.xml", TeamCityProjects.class);
	}

	private TeamCityProject createProject() {
		return (TeamCityProject) load("app/rest/projects/id:project54.xml", TeamCityProject.class);
	}

	private TeamCityBuilds createBuilds() {
		return (TeamCityBuilds) load("app/rest/builds.xml", TeamCityBuilds.class);
	}

	private Object load(String fileName, Class<?> clazz) {
		try {
			String file = ClasspathFiles.getUrlFile(fileName);
			URL url = new URL(file);
			JAXBContext newInstance = JAXBContext.newInstance(clazz);
			Unmarshaller unmarshaller = newInstance.createUnmarshaller();
			return unmarshaller.unmarshal(url);
		} catch (Exception t) {
			throw new RuntimeException(t);
		}
	}

}
