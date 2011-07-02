package net.awired.visuwall.plugin.sonar;

import javax.ws.rs.core.MediaType;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.plugin.sonar.resource.Projects;
import com.google.common.annotations.VisibleForTesting;

public class SonarClient {

    @VisibleForTesting
    GenericSoftwareClient client;

    private String url;

    public SonarClient() {
    }

    public SonarClient(String url) {
        this.url = url;
        this.client = new GenericSoftwareClient();
    }

    public Projects findProjects() throws ResourceNotFoundException {
        String projectsUrl = url + "/api/projects";
        Projects projects = client.resource(projectsUrl, Projects.class, MediaType.APPLICATION_XML_TYPE);
        return projects;
    }

}
