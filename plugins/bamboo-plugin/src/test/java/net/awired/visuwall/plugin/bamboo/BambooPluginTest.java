package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;

import org.junit.Before;
import org.junit.Test;

public class BambooPluginTest {

    BambooPlugin bamboo;

    @Before
    public void init() {
        bamboo = new BambooPlugin();
    }

    @Test
    public void should_get_name() {
        assertEquals("Bamboo Plugin", bamboo.getName());
    }

    @Test
    public void should_get_version() {
        assertEquals(1.0f, bamboo.getVersion(), 0);
    }

    @Test
    public void should_get_connection_class() {
        assertEquals(BambooConnection.class, bamboo.getConnectionClass());
    }

    @Test
    public void should_get_software_id_without_version() throws MalformedURLException {
        URL url = new URL("http://bamboo:8080");
        SoftwareId softwareId = bamboo.getSoftwareId(url);
        assertEquals("Bamboo", softwareId.getName());
        assertEquals("version not found", softwareId.getVersion());
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_url_parameter() {
        bamboo.getSoftwareId(null);
    }

    @Test
    public void should_get_a_valid_connection() {
        BambooConnection connection = bamboo.getConnection("url", null);
        assertFalse(connection.isClosed());
    }

}
