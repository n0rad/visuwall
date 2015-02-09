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

package fr.norad.visuwall.plugin.sonar;

import static javax.ws.rs.core.MediaType.APPLICATION_XML_TYPE;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URL;
import java.util.HashMap;

import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
@SuppressWarnings("unchecked")
public class SonarPluginTest {

    @InjectMocks
    SonarPlugin sonar;

    @Mock
    GenericSoftwareClient client;

    @Mock
    SonarConnectionFactory sonarConnectionFactory;

    @Mock
    SonarVersionExtractor sonarVersionExtractor;

    @Mock
    SonarDetector sonarDetector;

    @Test(expected = NullPointerException.class)
    public void should_throw_exception_when_passing_null_to_is_sonar_instance() throws SoftwareNotFoundException {
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
        URL url = new URL("http://sonar:9000");

        when(sonarDetector.isSonarPropertiesPage(url)).thenReturn(true);
        when(sonarVersionExtractor.propertiesVersion(any(Properties.class))).thenReturn("1.3");

        SoftwareId softwareId = sonar.getSoftwareId(url);

        assertEquals("Sonar", softwareId.getName());
        assertEquals("1.3", softwareId.getVersion());
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_throw_exception_if_url_is_not_sonar() throws Exception {
        when(client.exist(anyString(), any(Class.class))).thenReturn(false);

        URL url = new URL("http://www.google.fr");
        sonar.getSoftwareId(url);
    }

    @Test(expected = SoftwareNotFoundException.class)
    public void should_throw_exception_if_there_is_no_properties_page() throws Exception {

        Object resourceCall = client.resource(anyString(), any(Class.class), eq(APPLICATION_XML_TYPE));
        when(resourceCall).thenThrow(new ResourceNotFoundException("not found"));

        URL url = new URL("http://www.google.fr");
        sonar.getSoftwareId(url);
    }

    @Test
    public void should_get_connection() throws Exception {
        SonarConnection mockedConnection = new SonarConnection();

        when(sonarConnectionFactory.create(anyString(), anyString(), anyString())).thenReturn(mockedConnection);

        SonarConnection connection = sonar.getConnection(new URL("http://url"), new HashMap<String, String>());
        assertEquals(mockedConnection, connection);
    }

    @Test
    public void should_get_incompatible_property_when_version_is_inferior_of_24() throws Exception {
        when(sonarDetector.isSonarPropertiesPage(any(URL.class))).thenReturn(true);
        when(sonarVersionExtractor.propertiesVersion(any(Properties.class))).thenReturn("1.3");

        URL url = new URL("http://sonar.com/v13");
        SoftwareId softwareId = sonar.getSoftwareId(url);

        assertFalse(softwareId.isCompatible());
        assertEquals("Sonar version 1.3 is not compatible with Visuwall. Please use a version >= 2.4",
                softwareId.getWarnings());
    }

    @Test
    public void should_extract_version_from_welcome_page() throws Exception {
        when(sonarDetector.isSonarWelcomePage(any(URL.class))).thenReturn(true);

        when(sonarVersionExtractor.welcomePageVersion(any(URL.class))).thenReturn("2.5");

        URL url = new URL("http://sonar.com/v25");
        SoftwareId softwareId = sonar.getSoftwareId(url);

        assertTrue(softwareId.isCompatible());
    }
}
