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

package net.awired.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import org.junit.Before;
import org.junit.Test;

public class JenkinsPluginTest {

    JenkinsPlugin jenkinsPlugin;

    @Before
    public void init() {
        jenkinsPlugin = new JenkinsPlugin();
    }

    @Test(expected = NullPointerException.class)
    public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance()
            throws IncompatibleSoftwareException {
        jenkinsPlugin.getSoftwareId(null);
    }

    @Test
    public void should_create_valid_plugin() {
        assertEquals(JenkinsConnection.class, jenkinsPlugin.getConnectionClass());
        assertEquals("Jenkins plugin", jenkinsPlugin.getName());
        assertEquals(1.0f, jenkinsPlugin.getVersion(), 0);
    }

    @Test(expected = IncompatibleSoftwareException.class)
    public void should_throw_exception_when_software_url_cant_be_reached() throws Exception {
        URL url = new URL("http://notfound");
        jenkinsPlugin.getSoftwareId(url);
    }
}
