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

package fr.norad.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.net.MalformedURLException;
import java.net.URL;

import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;

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

    @Test(expected = SoftwareNotFoundException.class)
    public void should_not_get_software_id_without_version() throws Exception {
        URL url = new URL("http://bamboo:8080");
        SoftwareId softwareId = bamboo.getSoftwareId(url);
        assertEquals("Bamboo", softwareId.getName());
        assertEquals("version not found", softwareId.getVersion());
    }

    @Test(expected = NullPointerException.class)
    public void cant_pass_null_as_url_parameter() throws SoftwareNotFoundException {
        bamboo.getSoftwareId(null);
    }

    @Test
    public void should_get_a_valid_connection() throws MalformedURLException {
        BambooConnection connection = bamboo.getConnection(new URL("http://url"), null);
        assertFalse(connection.isClosed());
    }

}
