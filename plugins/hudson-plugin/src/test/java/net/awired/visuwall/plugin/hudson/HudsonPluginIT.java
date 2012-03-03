package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Test;

public class HudsonPluginIT {

    HudsonPlugin hudsonPlugin = new HudsonPlugin();

    String url = "http://localhost:8220";

    @Test
    public void should_connect_with_fduthu() throws Exception {
        URL hudsonUrl = new URL(url);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("login", "fduthu");
        properties.put("password", "password");

        HudsonConnection connection = hudsonPlugin.getConnection(hudsonUrl, properties);
        assertNotNull(connection);
        assertEquals(1, connection.listSoftwareProjectIds().size());
    }

    @Test
    public void should_connect_with_authenticated() throws Exception {
        URL hudsonUrl = new URL(url);

        Map<String, String> properties = new HashMap<String, String>();
        properties.put("login", "authenticated");
        properties.put("password", "password");

        HudsonConnection connection = hudsonPlugin.getConnection(hudsonUrl, properties);
        assertNotNull(connection);
        assertEquals(1, connection.listSoftwareProjectIds().size());
    }

    @Test
    public void should_connect_with_guest() throws Exception {
        URL hudsonUrl = new URL(url);
        SoftwareId softwareId = hudsonPlugin.getSoftwareId(hudsonUrl, null);
        assertNotNull(softwareId);
        assertTrue(softwareId.isCompatible());
    }
}
