package net.awired.visuwall.plugin.continuum;

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
        return 0;
    }

    @Override
    public String getName() {
        return "Continumm Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Continuum");
        softwareId.setVersion("0");
        softwareId.setWarnings("");
        return softwareId;
    }

}
