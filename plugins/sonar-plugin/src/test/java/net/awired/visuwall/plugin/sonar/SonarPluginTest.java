/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.visuwall.plugin.sonar;

import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URL;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class SonarPluginTest {

    SonarPlugin sonar;

    @Before
    public void init() {
        sonar = new SonarPlugin();
    }

    @Test(expected = NullPointerException.class)
    public void should_throw_exception_when_passing_null_to_is_sonar_instance() throws IncompatibleSoftwareException {
        new SonarPlugin().getSoftwareId(null);
    }

    @Test
    public void should_get_valid_class() {
        Class<SonarConnection> connectionClass = sonar.getConnectionClass();
        assertEquals(SonarConnection.class, connectionClass);
    }

    @Test
    public void should_get_valid_name() {
        assertEquals("Sonar plugin", sonar.getName());
    }

    @Test
    public void should_get_valid_version() {
        assertTrue(sonar.getVersion() > 0);
    }

    @Test
    public void should_get_software_id() throws Exception {
        Property property = new Property();
        property.setKey(SonarPlugin.SONAR_CORE_VERSION_KEY);
        property.setValue("1.0");

        Properties properties = new Properties();
        properties.getProperties().add(property);

        GenericSoftwareClient client = mock(GenericSoftwareClient.class);
        Object resourceCall = client.resource(anyString(), any(Class.class), eq(APPLICATION_XML_TYPE));
        when(resourceCall).thenReturn(properties);

        sonar.client = client;

        URL url = new URL("http://sonar:9000");
        SoftwareId softwareId = sonar.getSoftwareId(url);

        assertEquals("Sonar", softwareId.getName());
        assertEquals("1.0", softwareId.getVersion());
    }

    @Test(expected = IncompatibleSoftwareException.class)
    public void should_throw_exception_if_url_is_not_sonar() throws Exception {
        Properties properties = new Properties();

        GenericSoftwareClient client = mock(GenericSoftwareClient.class);
        Object resourceCall = client.resource(anyString(), any(Class.class), eq(APPLICATION_XML_TYPE));
        when(resourceCall).thenReturn(properties);

        sonar.client = client;

        URL url = new URL("http://www.google.fr");
        sonar.getSoftwareId(url);
    }

    @Test(expected = IncompatibleSoftwareException.class)
    public void should_throw_exception_if_there_is_no_properties_page() throws Exception {

        GenericSoftwareClient client = mock(GenericSoftwareClient.class);
        Object resourceCall = client.resource(anyString(), any(Class.class), eq(APPLICATION_XML_TYPE));
        when(resourceCall).thenThrow(new ResourceNotFoundException("not found"));

        sonar.client = client;

        URL url = new URL("http://www.google.fr");
        sonar.getSoftwareId(url);
    }

    @Test
    public void should_get_connection() throws ConnectionException {
        SonarConnection mockedConnection = new SonarConnection();

        SonarConnectionFactory sonarConnectionFactory = mock(SonarConnectionFactory.class);
        when(sonarConnectionFactory.create(anyString())).thenReturn(mockedConnection);

        sonar.sonarConnectionFactory = sonarConnectionFactory;

        SonarConnection connection = sonar.getConnection("url", null);
        assertEquals(mockedConnection, connection);
    }
}
