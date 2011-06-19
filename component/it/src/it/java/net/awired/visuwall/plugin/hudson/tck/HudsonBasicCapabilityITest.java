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

import static net.awired.visuwall.plugin.hudson.HudsonConnection.HUDSON_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HudsonBasicCapabilityITest implements BasicCapabilityTCK {

	BasicCapability hudson = new HudsonConnection();

    @Before
    public void setUp() throws ConnectionException {
		((Connection) hudson).connect(IntegrationTestData.HUDSON_URL, null, null);
    }

	@Override
	@Test
	public void should_find_all_projects_ids() {
        List<ProjectId> projects = hudson.findAllProjects();
        assertFalse(projects.isEmpty());
    }

	@Override
	@Test
	public void should_find_a_project() throws ProjectNotFoundException {
        ProjectId projectId = fluxxProjectId();
        Project project = hudson.findProject(projectId);
        assertNotNull(project);
    }

	@Override
	@Test
	public void should_find_project_ids_by_names() {
        List<String> names = Arrays.asList("fluxx", "visuwall");
        List<ProjectId> projectIds = hudson.findProjectsByNames(names);
        assertEquals(2, projectIds.size());
        assertEquals("fluxx", projectIds.get(0).getName());
        assertEquals("visuwall", projectIds.get(1).getName());
	}

	@Override
	@Test
	public void should_contain_project() {
        assertTrue(hudson.contains(fluxxProjectId()));
	}

	@Override
	@Test
	public void should_not_contain_project() {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "idValue");
        assertFalse(hudson.contains(projectId));
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
	public void should_get_disable_project() throws ProjectNotFoundException {

	}

    private ProjectId fluxxProjectId() {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "fluxx");
        return projectId;
    }

}
