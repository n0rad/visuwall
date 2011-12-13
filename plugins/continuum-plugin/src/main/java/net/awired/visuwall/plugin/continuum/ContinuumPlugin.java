package net.awired.visuwall.plugin.continuum;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class ContinuumPlugin implements VisuwallPlugin<ContinuumConnection> {

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
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        if (isManageable(url)) {
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Continuum");
            softwareId.setVersion("1.0");
            softwareId.setWarnings("");
            return softwareId;
        }
        throw new IncompatibleSoftwareException("Url " + url + " is not compatible with Continuum");
    }

    private boolean isManageable(URL url) {
        try {
            url = new URL(url.toString() + "/groupSummary.action");
            String content;
            content = Downloadables.getContent(url);
            return content.contains("Continuum");
        } catch (IOException e) {
            return false;
        }
    }
}
