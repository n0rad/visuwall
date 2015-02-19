/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;
import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;

public class TeamCityPluginTest {

    @Mock
    GenericSoftwareClient genericSoftwareClient;

    TeamCityPlugin plugin;

    static URL teamcityUrl;

    static {
        try {
            teamcityUrl = new URL("http://teamcity.com");
        } catch (MalformedURLException e) {
            throw new AssertionError(e);
        }
    }

    private Map<String, String> properties = new HashMap<>();

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);
        plugin = new TeamCityPlugin();
    }

    @Test
    public void should_get_a_connection() throws MalformedURLException {
        TeamCityConnection connection = plugin.getConnection(teamcityUrl, new HashMap<String, String>());
        assertFalse(connection.isClosed());
    }

    @Test(expected = NullPointerException.class)
    public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance()
            throws SoftwareNotFoundException {
        new TeamCityPlugin().getSoftwareId(null, properties);
    }

    @Test
    public void should_get_a_valid_plugin_info() {
        String name = plugin.getName();
        float version = plugin.getVersion();
        Class<TeamCityConnection> connectionClass = plugin.getConnectionClass();

        assertEquals("TeamCity plugin", name);
        assertEquals(1.0f, version, 0);
        assertEquals(TeamCityConnection.class, connectionClass);
        assertFalse(plugin.toString().isEmpty());
    }

    //todo need to mock client
//    @Test
//    public void should_get_valid_software_id() throws Exception {
//        TeamCityServer server = new TeamCityServer();
//        server.setVersionMajor(1);
//        server.setVersionMinor(0);
//        when(genericSoftwareClient.resource(anyString(), any(Class.class))).thenReturn(server);
//
//        SoftwareId softwareId = plugin.getSoftwareId(teamcityUrl, properties);
//
//        String name = softwareId.getName();
//        String version = softwareId.getVersion();
//        String warnings = softwareId.getWarnings();
//
//        assertEquals("TeamCity", name);
//        assertEquals("1.0", version);
//        assertEquals("", warnings);
//    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_throw_exception_when_software_is_not_compatible() throws Exception {
        Throwable notFound = new ResourceNotFoundException("not found");
        when(genericSoftwareClient.resource(anyString(), any(Class.class))).thenThrow(notFound);

        plugin.getSoftwareId(teamcityUrl, properties);
    }
}
