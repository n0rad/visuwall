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

import static net.awired.visuwall.IntegrationTestData.HUDSON_ID;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.hudson.HudsonConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class HudsonBasicCapabilityIT implements BasicCapabilityTCK {

	BasicCapability hudson = new HudsonConnection();

    @Before
	public void setUp() {
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
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "neverbuild");
        Project project = hudson.findProject(projectId);
        assertNotNull(project);
    }

	@Override
	@Test
	@Ignore
	public void should_find_project_ids_by_names() {
	}

	@Override
	@Test
	@Ignore
	public void should_contain_project() {
	}

	@Override
	@Test
	@Ignore
	public void should_not_contain_project() {
	}

	@Override
	@Test
	@Ignore
	public void should_find_all_project_names() {

	}

	@Override
	@Test
	@Ignore
	public void should_get_disable_project() throws ProjectNotFoundException {

	}

}
