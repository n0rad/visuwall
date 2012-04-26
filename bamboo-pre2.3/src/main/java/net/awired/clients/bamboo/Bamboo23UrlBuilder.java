package net.awired.clients.bamboo;

public class Bamboo23UrlBuilder {

    private static final String API_REST = "/api/rest";
    private String url;

    public Bamboo23UrlBuilder(String url) {
        this.url = url;
    }

    public String getLoginUrl(String login, String password) {
        return url + API_REST + "/login.action?username=" + login + "&password=" + password;
    }

    public String getBuildNamesUrl(String auth) {
        return url + API_REST + "/listBuildNames.action?" + auth(auth);
    }

    public String getLatestBuildResultsForProject(String auth, String projectKey) {
        return url + API_REST + "/getLatestBuildResultsForProject.action?" + auth(auth) + "&" + projectKey(projectKey);
    }

    public String getRecentlyCompletedBuildResultsForProject(String auth, String projectKey) {
        return url + API_REST + "/getRecentlyCompletedBuildResultsForProject.action?" + auth(auth) + "&"
                + projectKey(projectKey);
    }

    private String projectKey(String projectKey) {
        return "projectKey=" + projectKey;
    }

    private String auth(String auth) {
        return "auth=" + auth;
    }

}
