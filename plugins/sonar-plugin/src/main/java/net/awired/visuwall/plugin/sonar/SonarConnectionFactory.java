package net.awired.visuwall.plugin.sonar;

import net.awired.visuwall.api.exception.ConnectionException;

public class SonarConnectionFactory {

    public SonarConnection create(String url) throws ConnectionException {
        SonarConnection sonarConnection = new SonarConnection();
        sonarConnection.connect(url);
        return sonarConnection;
    }

}
