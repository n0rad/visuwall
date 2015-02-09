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
package fr.norad.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;

import java.net.URL;

import fr.norad.visuwall.providers.common.GenericSoftwareClient;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

@RunWith(MockitoJUnitRunner.class)
public class SonarDetectorTest {

    @InjectMocks
    SonarDetector sonarDetector;

    @Mock
    GenericSoftwareClient client;

    @Test
    public void should_build_properties_url() throws Exception {
        URL url = new URL("http://localhost:9000");
        assertEquals("http://localhost:9000/api/properties/sonar.core.version", sonarDetector.buildPropertiesUrl(url));
    }

    @Test
    public void should_return_true_if_url_with_properties_exists() throws Exception {
        URL url = new URL("http://localhost:9000");

        when(client.exist(anyString(), any(Class.class))).thenReturn(true);

        assertTrue(sonarDetector.isSonarPropertiesPage(url));
    }

    @Test
    public void should_return_true_if_url_with_page_contains_Sonar() throws Exception {
        URL url = new URL("http://localhost:9000");

        when(client.contains(any(URL.class), eq("Sonar"))).thenReturn(true);

        assertTrue(sonarDetector.isSonarWelcomePage(url));
    }
}
