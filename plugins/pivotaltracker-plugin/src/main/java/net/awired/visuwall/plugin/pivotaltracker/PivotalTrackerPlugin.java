package net.awired.visuwall.plugin.pivotaltracker;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class PivotalTrackerPlugin implements VisuwallPlugin<PivotalTrackerConnection> {

    @Override
    public PivotalTrackerConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        String login = properties.get("login");
        String password = properties.get("password");
        PivotalTrackerConnection pivotalTrackerConnection = new PivotalTrackerConnection();
        pivotalTrackerConnection.connect(url.toString(), login, password);
        return pivotalTrackerConnection;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<PivotalTrackerConnection> getConnectionClass() {
        return PivotalTrackerConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "PivotalTracker plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setCompatible(true);
        softwareId.setName("PivotalTracker");
        return softwareId;
    }

}
