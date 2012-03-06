package net.awired.clients.deployit;

import static com.google.common.base.Preconditions.checkNotNull;

import java.util.ArrayList;
import java.util.List;

import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.deployit.resource.ArchivedTasks;
import net.awired.clients.deployit.resource.RepositoryObjectIds;

import org.apache.commons.lang3.StringUtils;

public class DeployIt {

    private GenericSoftwareClient client;

    private DeployItUrlBuilder urlBuilder;

    public DeployIt(String url, String login, String password) {
        checkNotNull(url, "url is mandatory");
        this.urlBuilder = new DeployItUrlBuilder(url);
        if (login != null && password != null) {
            this.client = new GenericSoftwareClient(login, password);
        } else {
            this.client = new GenericSoftwareClient();
        }
    }

    public ArchivedTasks getArchivedTasks() throws ResourceNotFoundException {
        String url = urlBuilder.getArchivedTasksUrl();
        return client.resource(url, ArchivedTasks.class);
    }

    public List<String> getEnvironmentNames() throws ResourceNotFoundException {
        String url = urlBuilder.getEnvironmentNamesUrl();
        RepositoryObjectIds repositoryObjectIds = client.resource(url, RepositoryObjectIds.class);
        List<String> environments = repositoryObjectIds.getRepositoryObjectIds();
        List<String> environmentNames = new ArrayList<String>();
        for (String environment : environments) {
            String environmentName = getEnvironmentName(environment);
            environmentNames.add(environmentName);
        }
        return environmentNames;
    }

    public List<String> getDeployedApplicationsByEnvironment(String environmentName) throws ResourceNotFoundException {
        String url = urlBuilder.getDeployedApplicationsNamesByEnvironment(environmentName);
        RepositoryObjectIds repositoryObjectIds = client.resource(url, RepositoryObjectIds.class);
        List<String> deployedApplications = repositoryObjectIds.getRepositoryObjectIds();
        List<String> deployedApplicationNames = new ArrayList<String>();
        for (String deployedApplication : deployedApplications) {
            String deployedApplicationName = getDeployedApplicationName(deployedApplication, environmentName);
            deployedApplicationNames.add(deployedApplicationName);
        }
        return deployedApplicationNames;
    }

    private String getDeployedApplicationName(String deployedApplication, String environmentName) {
        return StringUtils.remove(deployedApplication, "Environments/");
    }

    private String getEnvironmentName(String environment) {
        return StringUtils.remove(environment, "Environments/");
    }

}
