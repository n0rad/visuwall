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

import static net.awired.visuwall.IntegrationTestData.BAMBOO_URL;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.tck.BasicCapabilityTCK;
import net.awired.visuwall.plugin.bamboo.BambooConnection;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class BambooBasicCapabilityIT implements BasicCapabilityTCK {

	BasicCapability bamboo = new BambooConnection();

    @Before
    public void init() throws ConnectionException {
        bamboo.connect(BAMBOO_URL, null, null);
	}

	@Override
    @Test
	public void should_find_all_projects_ids() {
        List<SoftwareProjectId> projects = bamboo.findAllSoftwareProjectIds();
        assertFalse(projects.isEmpty());
    }

	@Override
	@Test
	public void should_find_project_ids_by_names() {
        String ajslName = "ajsl - Awired Java Standard Library 1.0-ALPHA6";
        String strutsName = "struts - struts";
        String struts2Name = "struts 2 instable - struts_2_instable";
        List<String> names = Arrays.asList(strutsName, struts2Name, ajslName);
        List<SoftwareProjectId> projectIds = bamboo.findSoftwareProjectIdsByNames(names);

        assertEquals("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", projectIds.get(0).getProjectId());
        assertEquals("STRUTS-STRUTS", projectIds.get(1).getProjectId());
        assertEquals("STRUTS2INSTABLE-STRUTS2INSTABLE", projectIds.get(2).getProjectId());
    }

	@Override
	@Test
	public void should_find_all_project_names() {
        List<String> names = bamboo.findProjectNames();
        assertFalse(names.isEmpty());
	}

	@Override
	@Test
	@Ignore
	public void should_get_disable_project() throws ProjectNotFoundException {

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
    
    @Test(expected = IllegalStateException.class)
    public void should_close_connection() {
        bamboo.close();
        bamboo.findSoftwareProjectIdsByNames(new ArrayList<String>());
    }


}
