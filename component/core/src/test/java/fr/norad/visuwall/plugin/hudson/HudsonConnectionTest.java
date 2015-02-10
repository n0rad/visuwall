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

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import fr.norad.visuwall.providers.hudson.Hudson;
import fr.norad.visuwall.providers.hudson.domain.HudsonJob;
import fr.norad.visuwall.providers.hudson.exception.HudsonJobNotFoundException;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.exception.ProjectNotFoundException;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class HudsonConnectionTest {

    @Mock
    Hudson hudson;

    HudsonConnection hudsonConnection;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        hudsonConnection = new HudsonConnection();
        hudsonConnection.connect("http://hudson:8080", "", "");
        hudsonConnection.hudson = hudson;
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Date date = new Date();
        when(hudson.getEstimatedFinishTime(Matchers.anyString())).thenReturn(date);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        assertEquals(date, hudsonConnection.getEstimatedFinishTime(projectId, ""));
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        when(hudson.getLastBuildNumber("project1")).thenReturn(5);

        SoftwareProjectId projectId = new SoftwareProjectId("project1");

        String lastBuildId = hudsonConnection.getLastBuildId(projectId);

        assertEquals("5", lastBuildId);
    }

    @Test
    public void should_get_a_disabled_project() throws Exception {
        HudsonJob job = new HudsonJob();
        job.setDisabled(true);

        when(hudson.findJob(anyString())).thenReturn(job);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = hudsonConnection.isProjectDisabled(softwareProjectId);

        assertTrue(isDisabled);
    }

    @Test
    public void should_get_an_enabled_project() throws Exception {
        HudsonJob job = new HudsonJob();
        job.setDisabled(false);

        when(hudson.findJob(anyString())).thenReturn(job);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = hudsonConnection.isProjectDisabled(softwareProjectId);

        assertFalse(isDisabled);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Throwable notFound = new HudsonJobNotFoundException("not found");
        when(hudson.findJob(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        hudsonConnection.isProjectDisabled(softwareProjectId);
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url() {
        new HudsonConnection().connect("", "", "");
    }

    @Test
    public void should_remove_default_view() {
        List<String> viewNames = asList("Alle", "Todo", "Tous", "\u3059\u3079\u3066", "Tudo", "\u0412\u0441\u0435",
                "Hepsi", "All");
        List<String> defaultViews = new ArrayList<String>(viewNames);
        when(hudson.findViews()).thenReturn(defaultViews);
        List<String> views = hudsonConnection.findViews();
        assertTrue(views.isEmpty());
    }

    @Test
    public void should_keep_custom_view() {
        List<String> defaultViews = new ArrayList<String>(asList("Tous", "MyCusomView"));
        when(hudson.findViews()).thenReturn(defaultViews);
        List<String> views = hudsonConnection.findViews();
        assertEquals(1, views.size());
    }

    @Test
    public void should_create_hudson() {
        new HudsonConnection().connect("url", "", "");
    }

}
