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

package net.awired.visuwall.plugin.teamcity.tck;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import java.util.Map;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.teamcity.TeamCityConnection;
import org.junit.Before;
import org.junit.Test;

public class TeamBasicCapabilityIT implements BasicCapabilityTCK {

    BasicCapability teamcity = new TeamCityConnection();

    @Before
    public void init() throws ConnectionException {
        teamcity.connect(IntegrationTestData.TEAMCITY_URL, "guest", "");
    }

    @Override
    @Test
    public void should_find_all_projects_ids() {
        Map<String, SoftwareProjectId> projects = teamcity.listSoftwareProjectIds();
        assertFalse(projects.isEmpty());
    }

    @Override
    @Test
    public void should_find_description_of_a_project() throws ProjectNotFoundException {
        SoftwareProjectId projectSoftwareId = amazonProjectSoftwareId();
        String description = teamcity.getDescription(projectSoftwareId);
        assertEquals("typica & maragogype", description);
    }

    @Override
    @Test
    public void should_identify_a_project() throws ProjectNotFoundException {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("Amazon API client");
        SoftwareProjectId softwareProjectId = teamcity.identify(projectKey);
        assertEquals("project54", softwareProjectId.getProjectId());
    }

    @Override
    @Test(expected = MavenIdNotFoundException.class)
    public void should_get_maven_id() throws Exception {
        teamcity.getMavenId(amazonProjectSoftwareId());
    }

    @Override
    @Test
    public void should_get_name_of_a_project() throws Exception {
        SoftwareProjectId projectSoftwareId = amazonProjectSoftwareId();
        String name = teamcity.getName(projectSoftwareId);
        assertEquals("Amazon API client", name);
    }

    @Override
    @Test
    public void should_get_a_disabled_project() throws Exception {
        SoftwareProjectId projectSoftwareId = amazonProjectSoftwareId();
        boolean isDisabled = teamcity.isProjectDisabled(projectSoftwareId);
        assertFalse(isDisabled);
    }

    private SoftwareProjectId amazonProjectSoftwareId() {
        return new SoftwareProjectId("project54");
    }

}
