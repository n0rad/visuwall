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
package fr.norad.visuwall.plugin.continuum;

import static org.junit.Assert.assertEquals;
import java.net.URL;
import java.util.Map;
import org.junit.Test;
import fr.norad.visuwall.api.domain.SoftwareId;

public class ContinuumPluginIT {

    private Map<String, String> properties;

    @Test
    public void test() throws Exception {
        ContinuumPlugin plugin = new ContinuumPlugin();
        URL url = new URL("http://vmbuild.apache.org/continuum");
        SoftwareId softwareId = plugin.getSoftwareId(url, properties);
        assertEquals("Continuum", softwareId.getName());
        assertEquals("1.0", softwareId.getVersion());
    }
}
