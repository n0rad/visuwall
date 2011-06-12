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

package net.awired.visuwall.plugin.teamcity;

import static net.awired.visuwall.IntegrationTestData.TEAMCITY_URL;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;

import org.junit.Test;

public class TeamCityPluginIT {

	@Test
	public void should_recognize_teamcity_instance_with_valid_url() throws Exception {
		TeamCityPlugin teamcityPlugin = new TeamCityPlugin();
		SoftwareId softwareId = teamcityPlugin.getSoftwareId(new URL(TEAMCITY_URL));
		assertEquals("TeamCity", softwareId.getName());
		assertEquals("6.5", softwareId.getVersion());
	}

	@Test(expected = IncompatibleSoftwareException.class)
	public void should_not_fail_if_url_is_not_manageable() throws Exception {
		TeamCityPlugin teamcityPlugin = new TeamCityPlugin();
		String url = "http://www.google.fr";
		teamcityPlugin.getSoftwareId(new URL(url));
	}

}
