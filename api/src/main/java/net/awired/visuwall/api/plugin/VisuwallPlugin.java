package net.awired.visuwall.api.plugin;

import java.net.URL;

public interface VisuwallPlugin {

    ConnectionPlugin connect(String url, java.util.Properties info);

    String getName();

    int getVersion();

    boolean isManageable(URL url);

}
