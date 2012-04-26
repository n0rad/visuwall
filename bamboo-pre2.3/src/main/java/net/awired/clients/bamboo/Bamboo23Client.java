package net.awired.clients.bamboo;

import net.awired.clients.bamboo.resource.Response;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Bamboo23Client extends GenericSoftwareClient {

    private Bamboo23UrlBuilder urlBuilder;
    private String auth;

    private Logger logger = LoggerFactory.getLogger(Bamboo23Client.class);

    public Bamboo23Client(String url, String login, String password) {
        urlBuilder = new Bamboo23UrlBuilder(url);
        String loginUrl = urlBuilder.getLoginUrl(login, password);
        logger.debug(loginUrl);
        try {
            Response response = resource(loginUrl, Response.class);
            auth = response.getAuth();
        } catch (ResourceNotFoundException e) {
            String errorMessage = "Cannot log in bamboo at " + url + " with login:'" + login + "'";
            try {
                net.awired.clients.bamboo.resource.Error error = resource(loginUrl,
                        net.awired.clients.bamboo.resource.Error.class);
                throw new IllegalStateException(errorMessage + ", cause:" + error.getErrors().get(0));
            } catch (ResourceNotFoundException e1) {
                throw new IllegalStateException(errorMessage, e1);
            }
        }
    }

    public Response getBuildNames() {
        String buildNamesUrl = urlBuilder.getBuildNamesUrl(auth);
        logger.debug(buildNamesUrl);
        try {
            return resource(buildNamesUrl, Response.class);
        } catch (ResourceNotFoundException e) {
            throw new IllegalStateException("Cannot get build names at " + buildNamesUrl, e);
        }
    }

    public Response getLatestBuildResultsForProject(String projectKey) {
        String latestBuildResultsForProjectUrl = urlBuilder.getLatestBuildResultsForProject(auth, projectKey);
        logger.debug(latestBuildResultsForProjectUrl);
        try {
            return resource(latestBuildResultsForProjectUrl, Response.class);
        } catch (ResourceNotFoundException e) {
            throw new IllegalStateException("Cannot get latest build results for project " + projectKey + " at "
                    + latestBuildResultsForProjectUrl, e);
        }
    }

    public Response getRecentlyCompletedBuildResultsForProject(String projectKey) {
        String recentlyCompletedBuildResultsForProject = urlBuilder.getRecentlyCompletedBuildResultsForProject(auth,
                projectKey);
        logger.debug(recentlyCompletedBuildResultsForProject);
        try {
            return resource(recentlyCompletedBuildResultsForProject, Response.class);
        } catch (ResourceNotFoundException e) {
            throw new IllegalStateException("Cannot get recently completed build results for project " + projectKey
                    + " at " + recentlyCompletedBuildResultsForProject, e);
        }
    }

}
