/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.providers.pivotal;

import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.providers.pivotal.resource.Project;
import fr.norad.visuwall.providers.pivotal.resource.Projects;
import fr.norad.visuwall.providers.pivotal.resource.Stories;
import fr.norad.visuwall.providers.pivotal.resource.Token;

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
