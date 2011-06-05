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
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TeamCityTest {

    @Test
    public void should_list_all_project_names() {
        TeamCityProjects teamcityProjects = createProjects();
        TeamCityJerseyClient teamcityJerseyClient = prepareClientFor(teamcityProjects);

        TeamCity teamcity = new TeamCity();
        teamcity.teamcityJerseyClient = teamcityJerseyClient;

        List<String> projectNames = teamcity.findProjectNames();
        assertFalse(projectNames.isEmpty());
        for (String s : projectNames) {
            assertNotNull(s);
        }
    }

    @Test
    public void should_find_all_project() {
        TeamCityProjects teamcityProjects = createProjects();
        TeamCityJerseyClient teamcityJerseyClient = prepareClientFor(teamcityProjects);

        TeamCity teamcity = new TeamCity();
        teamcity.teamcityJerseyClient = teamcityJerseyClient;

        List<TeamCityProject> projects = teamcity.findAllProjects();
        assertFalse(projects.isEmpty());
        for (TeamCityProject project : projects) {
            assertNotNull(project.name);
            assertNotNull(project.id);
            assertNotNull(project.href);
        }
    }

    @Test
    public void should_load_project() {
        TeamCityProject teamcityProject = createProject();
        TeamCityJerseyClient teamcityJerseyClient = prepareClientFor(teamcityProject);

        TeamCity teamcity = new TeamCity();
        teamcity.teamcityJerseyClient = teamcityJerseyClient;

        TeamCityProject project = teamcity.findProject("project54");
        assertEquals("http://teamcity.jetbrains.com/project.html?projectId=project54", project.webUrl);
        assertEquals("typica & maragogype", project.description);
        assertFalse(project.archived);
        assertEquals("Amazon API client", project.name);
        assertEquals("project54", project.id);
        assertEquals("/app/rest/projects/id:project54", project.href);
    }

    private TeamCityJerseyClient prepareClientFor(Object o) {
        WebResource resource = Mockito.mock(WebResource.class);
        when(resource.get(Mockito.any(Class.class))).thenReturn(o);

        Client client = Mockito.mock(Client.class);
        when(client.resource(Mockito.anyString())).thenReturn(resource);

        return new TeamCityJerseyClient(client);
    }

    private TeamCityProjects createProjects() {
        try {
            String file = ClasspathFiles.getAbsolutePathFile("app/rest/projects/projects.xml");
            URL url = new URL(file);
            JAXBContext newInstance = JAXBContext.newInstance(TeamCityProjects.class);
            Unmarshaller unmarshaller = newInstance.createUnmarshaller();
            return (TeamCityProjects) unmarshaller.unmarshal(url);
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

    private TeamCityProject createProject() {
        try {
            String file = ClasspathFiles.getAbsolutePathFile("app/rest/projects/id:project54.xml");
            URL url = new URL(file);
            JAXBContext newInstance = JAXBContext.newInstance(TeamCityProjects.class);
            Unmarshaller unmarshaller = newInstance.createUnmarshaller();
            return (TeamCityProject) unmarshaller.unmarshal(url);
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }
}
