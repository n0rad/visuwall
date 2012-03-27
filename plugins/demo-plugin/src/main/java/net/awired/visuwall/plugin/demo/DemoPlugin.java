package net.awired.visuwall.plugin.demo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class DemoPlugin implements VisuwallPlugin<DemoConnection> {

    private static final String DEMO_VISUWALL_CI = "http://demo.visuwall.ci";

    @Override
    public DemoConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        if (url == null || !DEMO_VISUWALL_CI.equals(url.toString())) {
            throw new ConnectionException(getName() + " is not compatible with url : " + url);
        }
        DemoConnection connection = new DemoConnection();
        connection.connect(url.toString(), null, null);
        return connection;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<DemoConnection> getConnectionClass() {
        return DemoConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        if (url == null || !DEMO_VISUWALL_CI.equals(url.toString())) {
            throw new SoftwareNotFoundException(getName() + " is not compatible with url : " + url);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("demo");
        softwareId.setCompatible(true);
        softwareId.setVersion("1.0");
        softwareId.setWarnings("This is a demo plugin");
        return softwareId;
    }

}
