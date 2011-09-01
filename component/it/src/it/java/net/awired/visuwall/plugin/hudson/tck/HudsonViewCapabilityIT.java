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
import java.util.Arrays;
import java.util.List;
import net.awired.visuwall.Urls;
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
        hudson.connect(Urls.FLUXX_HUDSON, null, null);
    }

    @Override
    @Test
    public void should_list_all_views() {
        List<String> views = hudson.findViews();
        assertEquals(3, views.size());
        assertTrue(views.contains("Tous"));
        assertTrue(views.contains("View1"));
        assertTrue(views.contains("View2"));
    }

    @Override
    @Test
    public void should_list_all_project_in_a_view() throws ViewNotFoundException {
        List<String> projectNames = hudson.findProjectNamesByView("View1");
        assertFalse(projectNames.isEmpty());
    }

    @Override
    @Test
    public void should_find_all_projects_of_views() throws ViewNotFoundException {
        List<String> projectView1 = hudson.findProjectNamesByView("View1");
        List<String> projectView2 = hudson.findProjectNamesByView("View2");

        List<String> views = Arrays.asList("View1", "View2");
        List<SoftwareProjectId> projectIds = hudson.findSoftwareProjectIdsByViews(views);

        assertEquals(projectView1.size() + projectView2.size(), projectIds.size());
    }

}
