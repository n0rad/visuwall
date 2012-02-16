package net.awired.clients.deployit;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class DeployItUrlBuilderTest {

    DeployItUrlBuilder urlBuilder = new DeployItUrlBuilder("http://localhost:4516");

    @Test
    public void should_build_archived_tasks_url() {
        assertEquals("http://localhost:4516/deployit/export/tasks", urlBuilder.getArchivedTasksUrl());
    }

    @Test
    public void should_build_environment_names_url() {
        assertEquals("http://localhost:4516/deployit/query?typeName=udm.Environment",
                urlBuilder.getEnvironmentNamesUrl());
    }

    @Test
    public void should_build_deployed_applications_by_environment_url() {
        String environmentName = "Tomcat-Dev";
        String url = urlBuilder.getDeployedApplicationsNamesByEnvironment(environmentName);
        assertEquals(
                "http://localhost:4516/deployit/query?parent=Environments/Tomcat-Dev&typeName=udm.DeployedApplication",
                url);
    }

}
