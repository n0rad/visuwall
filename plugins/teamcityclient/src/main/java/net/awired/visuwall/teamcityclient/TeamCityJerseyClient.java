package net.awired.visuwall.teamcityclient;

import java.util.List;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class TeamCityJerseyClient {

    private Client client;

    public TeamCityJerseyClient(Client client) {
        this.client = client;
    }

    public List<TeamCityProject> getProjects() {
        WebResource resource = client.resource("url");
        TeamCityProjects teamCityProjects = resource.get(TeamCityProjects.class);
        return teamCityProjects.projects;
    }

    public TeamCityProject getProject(String projectId) {
        WebResource resource = client.resource("url");
        TeamCityProject teamCityProject = resource.get(TeamCityProject.class);
        return teamCityProject;
    }

}
