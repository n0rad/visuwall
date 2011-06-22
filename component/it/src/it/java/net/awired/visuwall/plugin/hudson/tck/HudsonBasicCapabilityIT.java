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

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HudsonBasicCapabilityIT implements BasicCapabilityTCK {

	BasicCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
        hudson.connect(IntegrationTestData.HUDSON_URL, null, null);
    }

	@Override
	@Test
	public void should_find_all_projects_ids() {
        List<SoftwareProjectId> projects = hudson.findAllSoftwareProjectIds();
        assertFalse(projects.isEmpty());
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
	public void should_find_all_project_names() {
        List<String> names = hudson.findProjectNames();
        List<String> hudsonNames = Arrays.asList("fluxx", "visuwall", "dev-radar");
        for (String hudsonName : hudsonNames) {
            assertTrue(names.contains(hudsonName));
        }
	}

    @Override
    @Test
    @Ignore
    public void should_find_description_of_a_project() throws ProjectNotFoundException {
    }

    @Override
    @Test
    @Ignore
    public void should_identify_a_project() throws ProjectNotFoundException {
    }

    @Override
    @Test
    @Ignore
    public void should_get_maven_id() throws Exception {
    }

    @Override
    @Test
    @Ignore
    public void should_get_name_of_a_project() throws Exception {

    }

}
