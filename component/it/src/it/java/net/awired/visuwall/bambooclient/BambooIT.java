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

package net.awired.visuwall.bambooclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooProjectNotFoundException;

import org.junit.Test;

public class BambooIT {

    private Bamboo bamboo = new Bamboo(IntegrationTestData.BAMBOO_URL);

    @Test
    public void should_find_all_projects() {
        List<BambooProject> projects = bamboo.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
	public void should_find_an_existing_project() throws BambooProjectNotFoundException {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        assertNotNull(project);
    }

    @Test
	public void should_fill_current_build_when_finding_project() throws BambooProjectNotFoundException {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        BambooBuild build = project.getCurrentBuild();
        assertEquals(3, build.getBuildNumber());
    }

    @Test
	public void should_fill_build_numbers_when_finding_project() throws BambooProjectNotFoundException {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        int[] buildNumbers = project.getBuildNumbers();
        assertEquals(buildNumbers[0], 3);
        assertEquals(buildNumbers[1], 2);
    }
}
