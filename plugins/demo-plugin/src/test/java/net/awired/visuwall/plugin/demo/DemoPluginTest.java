package net.awired.visuwall.plugin.demo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;

import org.junit.Test;

public class DemoPluginTest {

    DemoPlugin plugin = new DemoPlugin();

    @Test(expected = ConnectionException.class)
    public void should_not_get_a_connection_for_url_other_than_demo() throws Exception {
        URL url = new URL("http://something.else");
        DemoConnection connection = plugin.getConnection(url, null);
        assertNull(connection);
    }

    @Test
    public void should_get_a_connect_for_demo_url() throws Exception {
        URL url = new URL("http://demo.visuwall.ci");
        DemoConnection connection = plugin.getConnection(url, null);
        assertNotNull(connection);
    }

    @Test
    public void should_get_empty_map_for_properties() {
        Map<String, String> properties = plugin.getPropertiesWithDefaultValue();
        assertTrue(properties.isEmpty());
    }

    @Test
    public void should_get_demo_connection_class() {
        Class<DemoConnection> connectionClass = plugin.getConnectionClass();
        assertEquals(DemoConnection.class, connectionClass);
    }

    @Test
    public void should_get_version_1_0() {
        assertEquals(1.0f, plugin.getVersion(), 0);
    }

    @Test
    public void should_get_name() {
        assertEquals("Demo Plugin", plugin.getName());
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_get_null_for_null_url() throws SoftwareNotFoundException {
        assertNull(plugin.getSoftwareId(null));
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_get_null_for_invalid_url() throws Exception {
        assertNull(plugin.getSoftwareId(new URL("http://something.else")));
    }

    @Test
    public void should_get_software_id_for_valid_url() throws MalformedURLException, SoftwareNotFoundException {
        SoftwareId softwareId = plugin.getSoftwareId(new URL("http://demo.visuwall.ci"));
        assertEquals("demo", softwareId.getName());
        assertEquals("1.0", softwareId.getVersion());
        assertEquals("This is a demo plugin", softwareId.getWarnings());
        assertTrue(softwareId.isCompatible());
    }

}
