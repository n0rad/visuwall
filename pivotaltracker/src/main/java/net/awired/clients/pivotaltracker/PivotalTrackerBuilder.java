package net.awired.clients.pivotaltracker;

public class PivotalTrackerBuilder {

    private static final String API = "/services/v3";
    private String url;

    public PivotalTrackerBuilder(String url) {
        this.url = url;
    }

    public String getAuthenticationTokenUrl() {
        return url + API + "/tokens/active";
    }

    public String getAllProjectsUrl() {
        return url + API + "/projects";
    }

    public String getAllStoriesUrl(int projectId) {
        return url + API + "/projects/" + projectId + "/stories";
    }

}
