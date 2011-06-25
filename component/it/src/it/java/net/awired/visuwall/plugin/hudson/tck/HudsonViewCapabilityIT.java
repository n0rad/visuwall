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
import static org.junit.Assert.assertTrue;
import java.util.Arrays;
import java.util.List;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.api.plugin.tck.ViewCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;
import org.junit.Before;
import org.junit.Test;

public class HudsonViewCapabilityIT implements ViewCapabilityTCK {

    ViewCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
        hudson.connect(IntegrationTestData.HUDSON_URL, null, null);
    }

    @Override
    @Test
    public void should_list_all_views() {
        List<String> views = hudson.findViews();
        assertEquals(2, views.size());
        assertTrue(views.contains("View1"));
        assertTrue(views.contains("View2"));
    }

    @Override
    @Test
    public void should_list_all_project_in_a_view() throws ViewNotFoundException {
        List<String> projectNames = hudson.findProjectNamesByView("View1");
        assertEquals(2, projectNames.size());
        assertTrue(projectNames.contains("client-teamcity"));
        assertTrue(projectNames.contains("dev-radar"));
    }

    @Override
    @Test
    public void should_find_project_ids_by_names() {
        List<String> names = Arrays.asList("fluxx", "visuwall");
        List<SoftwareProjectId> projectIds = hudson.findSoftwareProjectIdsByNames(names);
        assertEquals(2, projectIds.size());
        assertEquals("fluxx", projectIds.get(0).getProjectId());
        assertEquals("visuwall", projectIds.get(1).getProjectId());
    }

    @Override
    @Test
    public void should_find_all_projects_of_views() {
        List<String> views = Arrays.asList("View1", "View2");
        List<SoftwareProjectId> projectIds = hudson.findSoftwareProjectIdsByViews(views);
        assertEquals(5, projectIds.size());
        List<String> names = Arrays.asList("itcoverage-project", "dev-radar", "fluxx", "dev-radar-sonar",
                "client-teamcity");
        for (int i = 0; i < projectIds.size(); i++) {
            assertTrue(names.contains(projectIds.get(i).getProjectId()));
        }
    }

}
