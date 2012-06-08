package net.awired.clients.pivotaltracker;

public class PivotalTrackerUrlBuilder {

    private static final String API = "/services/v3";
    private String url;

    public PivotalTrackerUrlBuilder(String url) {
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

    public String getProjectUrl(int projectId) {
        return url + API + "/projects/" + projectId;
    }

}
