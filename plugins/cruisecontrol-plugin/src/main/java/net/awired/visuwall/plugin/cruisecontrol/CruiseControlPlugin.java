package fr.norad.visuwall.plugin.cruisecontrol;

import java.net.URL;
import java.util.Map;
import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;
import fr.norad.visuwall.api.plugin.VisuwallPlugin;

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
    public SoftwareId getSoftwareId(URL url) throws SoftwareNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
