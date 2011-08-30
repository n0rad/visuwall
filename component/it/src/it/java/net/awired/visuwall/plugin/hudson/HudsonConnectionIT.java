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

package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonConnectionIT {

    static HudsonConnection hudsonConnection;

    @BeforeClass
    public static void init() {
        hudsonConnection = new HudsonConnection();
        hudsonConnection.connect(Urls.FLUXX_HUDSON);
    }

    @Test
    public void should_find_freestyle_project() {
        List<String> names = hudsonConnection.findProjectNames();
        assertTrue(names.contains("client-teamcity"));
    }

    @Test
    public void should_find_name_of_freestyle_project() throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("client-teamcity");
        String name = hudsonConnection.getName(softwareProjectId);
        assertEquals("client-teamcity", name);
    }
}
