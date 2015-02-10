package net.awired.visuwall.plugin.deployit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.GenericSoftwareClientFactory;
import net.awired.clients.deployit.resource.RepositoryObjectIds;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class DeployItPlugin implements VisuwallPlugin<DeployItConnection> {

    private final static Logger LOG = LoggerFactory.getLogger(DeployItPlugin.class);

    private GenericSoftwareClient client;
    private GenericSoftwareClientFactory factory;

    public DeployItPlugin() {
        LOG.info("DeployIt plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public DeployItConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        DeployItConnection connectionPlugin = new DeployItConnection();
        connectionPlugin.connect(url.toString(), properties.get("login"), properties.get("password"));
        return connectionPlugin;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        Map<String, String> properties = new HashMap<String, String>();
        return properties;
    }

    @Override
    public Class<DeployItConnection> getConnectionClass() {
        return DeployItConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "DeployIt Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        client = factory.createClient(properties);
        if (isDeployIt(url.toString())) {
            return createSoftwareId();
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with DeployIt");
    }

    private SoftwareId createSoftwareId() {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("DeployIt");
        softwareId.setVersion("unknown");
        softwareId.setWarnings("");
        return softwareId;
    }

    private boolean isDeployIt(String url) {
        String serverUrl = url + "/deployit/query";
        return client.exist(serverUrl, RepositoryObjectIds.class);
    }
}
