package net.awired.clients.deployit;

import static com.google.common.base.Preconditions.checkNotNull;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.deployit.resource.ArchivedTasks;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class DeployIt {

    private static final Logger LOG = LoggerFactory.getLogger(DeployIt.class);

    private GenericSoftwareClient client;

    private DeployItUrlBuilder urlBuilder;

    public DeployIt(String url, String login, String password) {
        checkNotNull(url, "url is mandatory");
        this.urlBuilder = new DeployItUrlBuilder(url);
        this.client = new GenericSoftwareClient(login, password);
    }

    public ArchivedTasks getArchivedTasks() throws ResourceNotFoundException {
        String url = urlBuilder.getArchivedTasksUrl();
        return client.resource(url, ArchivedTasks.class);
    }
}
