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

package net.awired.visuwall.plugin.bamboo.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import java.util.Map;
import net.awired.visuwall.Urls;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.bamboo.BambooConnection;
import org.junit.Before;
import org.junit.Test;

public class BambooBasicCapabilityIT implements BasicCapabilityTCK {

    BasicCapability bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
        bamboo.connect(Urls.LOCAL_BAMBOO, null, null);
    }

    public void should_find_all_projects_ids() {
        Map<SoftwareProjectId, String> projects = bamboo.listSoftwareProjectIds();
        assertFalse(projects.isEmpty());
    }

    @Override
    @Test
    public void should_find_description_of_a_project() throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("struts");
        String description = bamboo.getDescription(softwareProjectId);
        assertEquals("", description);
    }

    @Override
    @Test
    public void should_identify_a_project() throws ProjectNotFoundException {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("it coverage - itcoverage-plan");
        SoftwareProjectId softwareProjectId = bamboo.identify(projectKey);
        assertEquals("ITC-ITCOVERAGEKEY", softwareProjectId.getProjectId());
    }

    @Override
    @Test(expected = MavenIdNotFoundException.class)
    public void should_get_maven_id() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("ITC-ITCOVERAGEKEY");
        bamboo.getMavenId(softwareProjectId);
    }

    @Override
    @Test
    public void should_get_name_of_a_project() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("STR-STRUTSKEY");
        String name = bamboo.getName(softwareProjectId);
        assertEquals("struts", name);
    }

    @Override
    @Test
    public void should_get_a_disabled_project() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("SUC-SUCCESS");
        boolean isDisabled = bamboo.isProjectDisabled(softwareProjectId);
        assertTrue(isDisabled);
    }
}
