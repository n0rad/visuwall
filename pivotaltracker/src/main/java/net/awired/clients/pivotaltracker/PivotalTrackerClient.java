package net.awired.clients.pivotaltracker;

import java.util.HashMap;
import java.util.Map;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.pivotaltracker.resource.Project;
import net.awired.clients.pivotaltracker.resource.Projects;
import net.awired.clients.pivotaltracker.resource.Stories;
import net.awired.clients.pivotaltracker.resource.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class PivotalTrackerClient extends GenericSoftwareClient {

    private PivotalTrackerUrlBuilder urlBuilder;
    private String authenticationToken;

    private Logger logger = LoggerFactory.getLogger(PivotalTrackerClient.class);

    public PivotalTrackerClient(String url, String login, String password) {
        super(login, password);
        urlBuilder = new PivotalTrackerUrlBuilder(url);
        String authenticationTokenUrl = urlBuilder.getAuthenticationTokenUrl();
        logger.debug(authenticationTokenUrl);
        try {
            Token token = resource(authenticationTokenUrl, Token.class);
            authenticationToken = token.getGuid();
        } catch (ResourceNotFoundException e) {
            String errorMessage = "Cannot log in pivotaltracker at " + url + " with login:'" + login + "'";
            throw new IllegalStateException(errorMessage, e);
        }
    }

    public Projects getProjects() throws ResourceNotFoundException {
        String allProjectsUrl = urlBuilder.getAllProjectsUrl();
        return (Projects) resourceWithHeaders(allProjectsUrl, Projects.class);
    }

    public Stories getStoriesOf(int projectId) throws ResourceNotFoundException {
        String allStoriesUrl = urlBuilder.getAllStoriesUrl(projectId);
        return (Stories) resourceWithHeaders(allStoriesUrl, Stories.class);
    }

    public Project getProject(int projectId) throws ResourceNotFoundException {
        String projectUrl = urlBuilder.getProjectUrl(projectId);
        return (Project) resourceWithHeaders(projectUrl, Project.class);
    }

    private Object resourceWithHeaders(String allProjectsUrl, Class<?> clazz) throws ResourceNotFoundException {
        Map<String, String> headers = new HashMap<String, String>();
        headers.put("X-TrackerToken", authenticationToken);
        return resource(allProjectsUrl, clazz, headers);
    }

}
