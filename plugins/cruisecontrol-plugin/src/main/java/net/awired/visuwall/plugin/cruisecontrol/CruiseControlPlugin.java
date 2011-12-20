package net.awired.visuwall.plugin.cruisecontrol;

import java.net.URL;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class CruiseControlPlugin implements VisuwallPlugin<CruiseControlConnection> {

    @Override
    public CruiseControlConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Class<CruiseControlConnection> getConnectionClass() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public float getVersion() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public String getName() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        // TODO Auto-generated method stub
        return null;
    }

}
