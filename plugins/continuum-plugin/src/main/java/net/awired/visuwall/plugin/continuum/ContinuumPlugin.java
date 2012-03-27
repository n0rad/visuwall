package net.awired.visuwall.plugin.continuum;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContinuumPlugin implements VisuwallPlugin<ContinuumConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(ContinuumPlugin.class);

    public ContinuumPlugin() {
        LOG.info("Continuum plugin loaded.");
    }

    @Override
    public ContinuumConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        ContinuumConnection continuumConnection = new ContinuumConnection();
        continuumConnection.connect(url.toString(), "", "");
        return continuumConnection;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<ContinuumConnection> getConnectionClass() {
        return ContinuumConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "Continumm Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        if (isManageable(url)) {
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Continuum");
            softwareId.setVersion("1.0");
            softwareId.setWarnings("");
            return softwareId;
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with Continuum");
    }

    private boolean isManageable(URL url) {
        try {
            url = new URL(url.toString() + "/groupSummary.action");
            String content;
            content = new GenericSoftwareClient().download(url);
            return content.contains("Continuum");
        } catch (IOException e) {
            return false;
        } catch (ResourceNotFoundException e) {
            return false;
        }
    }
}
