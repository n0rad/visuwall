package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.net.URL;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class BambooPluginIT {

    @Test
    public void should_get_version() throws Exception {
        BambooPlugin bambooPlugin = new BambooPlugin();
        SoftwareId softwareId = bambooPlugin.getSoftwareId(new URL("http://localhost:8085"),
                bambooPlugin.getPropertiesWithDefaultValue());
        assertEquals("2.1.5", softwareId.getVersion());
        assertTrue(softwareId.isCompatible());
        System.err.println(softwareId);
    }

    @Test
    public void should_get_connection() throws Exception {
        BambooPlugin bambooPlugin = new BambooPlugin();
        Map<String, String> properties = bambooPlugin.getPropertiesWithDefaultValue();
        properties.put("login", "admin");
        properties.put("password", "admin");
        BambooConnection connection = bambooPlugin.getConnection(new URL("http://localhost:8085"), properties);
        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        assertEquals(1, softwareProjectIds.keySet().size());
    }

}
