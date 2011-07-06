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

package net.awired.visuwall.plugin.bamboo;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import java.util.List;
import net.awired.clients.bamboo.Bamboo;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.rest.Plan;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class BambooConnectionTest {

    BambooConnection bambooConnection = new BambooConnection();

    @Mock
    Bamboo bamboo;

    @Before
    public void init() {
        MockitoAnnotations.initMocks(this);

        bambooConnection.connect("http://bamboo:8080");
        bambooConnection.bamboo = bamboo;
    }

    @Test
    public void should_get_a_disabled_project() throws Exception {
        Plan plan = new Plan();
        plan.setEnabled(false);

        when(bamboo.findPlan(anyString())).thenReturn(plan);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = bambooConnection.isProjectDisabled(softwareProjectId);

        assertTrue(isDisabled);
    }

    @Test
    public void should_get_an_enabled_project() throws Exception {
        Plan plan = new Plan();
        plan.setEnabled(true);

        when(bamboo.findPlan(anyString())).thenReturn(plan);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        boolean isDisabled = bambooConnection.isProjectDisabled(softwareProjectId);

        assertFalse(isDisabled);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_is_not_found() throws Exception {
        Throwable notFound = new BambooPlanNotFoundException("not found");
        when(bamboo.findPlan(anyString())).thenThrow(notFound);

        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");
        bambooConnection.isProjectDisabled(softwareProjectId);
    }

    @Test
    public void should_return_empty_list_because_there_is_no_commiter_infos_in_rest_api() throws Exception {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("projectId");

        List<Commiter> buildCommiters = bambooConnection.getBuildCommiters(softwareProjectId, 0);

        assertTrue(buildCommiters.isEmpty());
    }

}
