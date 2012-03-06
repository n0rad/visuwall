package net.awired.clients.common;

import java.util.Map;

import net.awired.clients.common.GenericSoftwareClient;

public class GenericSoftwareClientFactory {

    public GenericSoftwareClient createClient(Map<String, String> properties) {
        if (properties != null && properties.containsKey("login") && properties.containsKey("password")) {
            String login = properties.get("login");
            String password = properties.get("password");
            return new GenericSoftwareClient(login, password);
        }
        return new GenericSoftwareClient();
    }

}
