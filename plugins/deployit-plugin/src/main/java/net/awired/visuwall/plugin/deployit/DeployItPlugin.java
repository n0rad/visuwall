package net.awired.visuwall.plugin.deployit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.clients.common.GenericSoftwareClient;
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

    private GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient("admin", "admin");

    public DeployItPlugin() {
        LOG.info("DeployIt plugin loaded.");
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
        properties.put("login", "admin");
        properties.put("password", "admin");
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
        return genericSoftwareClient.exist(serverUrl, RepositoryObjectIds.class);
    }
}
