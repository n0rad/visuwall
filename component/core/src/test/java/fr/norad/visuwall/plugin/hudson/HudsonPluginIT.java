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
package fr.norad.visuwall.plugin.hudson;

import java.net.URL;

import fr.norad.visuwall.api.domain.SoftwareProjectId;

import org.junit.Test;

public class HudsonPluginIT {

    final HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @Test
    public void should_check_plugin_management() throws Exception {
        URL hudsonUrl = new URL("http://ci.visuwall.awired.net/");
        HudsonConnection connection = hudsonPlugin.getConnection(hudsonUrl,
                hudsonPlugin.getPropertiesWithDefaultValue());
        SoftwareProjectId projectId = new SoftwareProjectId("struts 2 instable");
        System.out.println(connection.isBuilding(projectId, "5"));
        System.out.println(connection.isBuilding(projectId, "4"));

    }

}
