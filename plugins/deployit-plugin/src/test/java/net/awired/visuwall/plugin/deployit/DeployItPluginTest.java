package net.awired.visuwall.plugin.deployit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.MalformedURLException;
import java.net.URL;

import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.GenericSoftwareClientFactory;
import net.awired.clients.deployit.resource.RepositoryObjectIds;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class DeployItPluginTest {

    @InjectMocks
    DeployItPlugin plugin;

    @Mock
    GenericSoftwareClientFactory clientFactory;

    @Mock
    GenericSoftwareClient genericSoftwareClient;

    @Test
    public void should_get_a_connect_for_deployit_url() throws Exception {
        URL url = new URL("http://deployit:4516");
        DeployItConnection connection = plugin.getConnection(url, plugin.getPropertiesWithDefaultValue());
        assertNotNull(connection);
    }

    @Test
    public void should_get_demo_connection_class() {
        Class<DeployItConnection> connectionClass = plugin.getConnectionClass();
        assertEquals(DeployItConnection.class, connectionClass);
    }

    @Test
    public void should_get_version_1_0() {
        assertEquals(1.0f, plugin.getVersion(), 0);
    }

    @Test
    public void should_get_name() {
        assertEquals("DeployIt Plugin", plugin.getName());
    }

    @Test(expected = NullPointerException.class)
    public void should_get_null_for_null_url() throws SoftwareNotFoundException {
        assertNull(plugin.getSoftwareId(null, null));
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_get_null_for_invalid_url() throws Exception {
        when(clientFactory.createClient(Mockito.anyMap())).thenReturn(genericSoftwareClient);
        assertNull(plugin.getSoftwareId(new URL("http://something.else"), null));
    }

    @Test
    public void should_get_software_id_for_valid_url() throws MalformedURLException, SoftwareNotFoundException {
        URL url = new URL("http://deployit:4516");
        when(clientFactory.createClient(Mockito.anyMap())).thenReturn(genericSoftwareClient);
        when(genericSoftwareClient.exist(anyString(), eq(RepositoryObjectIds.class))).thenReturn(true);

        SoftwareId softwareId = plugin.getSoftwareId(url, null);
        assertEquals("DeployIt", softwareId.getName());
        assertEquals("unknown", softwareId.getVersion());
        assertEquals("", softwareId.getWarnings());
        assertTrue(softwareId.isCompatible());
    }
}
