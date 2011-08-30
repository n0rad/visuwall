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

package net.awired.visuwall.plugin.hudson.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import org.junit.Before;
import org.junit.Test;

public class HudsonBasicCapabilityIT implements BasicCapabilityTCK {

    BasicCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
        hudson.connect(Urls.FLUXX_HUDSON, null, null);
    }

    @Override
    @Test
    public void should_find_all_projects_ids() {
        Map<String, SoftwareProjectId> projects = hudson.listSoftwareProjectIds();
        assertFalse(projects.isEmpty());
    }

    @Override
    @Test
    public void should_find_description_of_a_project() throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("visuwall");
        String description = hudson.getDescription(softwareProjectId);
        assertEquals("keep an eye on your projects!", description);
    }

    @Override
    @Test
    public void should_identify_a_project() throws ProjectNotFoundException {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("visuwall");
        projectKey.setMavenId("net.awired.visuwall:visuwall");
        SoftwareProjectId identity = hudson.identify(projectKey);
        assertEquals(new SoftwareProjectId("visuwall"), identity);
    }

    @Override
    @Test
    public void should_get_maven_id() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("visuwall");
        String mavenId = hudson.getMavenId(softwareProjectId);
        assertEquals("net.awired.visuwall:visuwall", mavenId);
    }

    @Override
    @Test
    public void should_get_name_of_a_project() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("visuwall");
        String name = hudson.getName(softwareProjectId);
        assertEquals("visuwall", name);
    }

    @Override
    @Test
    public void should_get_a_disabled_project() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("neverbuild");
        boolean isDisabled = hudson.isProjectDisabled(softwareProjectId);
        assertTrue(isDisabled);
    }
}
