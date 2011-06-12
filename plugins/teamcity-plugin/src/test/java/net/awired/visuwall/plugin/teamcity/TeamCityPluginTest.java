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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.mockito.Mockito.when;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.teamcityclient.ClasspathFiles;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class TeamCityPluginTest {

	@Test(expected = NullPointerException.class)
	public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance()
	        throws IncompatibleSoftwareException {
		new TeamCityPlugin().getSoftwareId(null);
	}

	@Ignore
	@Test
	public void should_be_manageable() throws Exception {
		String strUrl = ClasspathFiles.getUrlFile("teamcity_version_page.xml");
		URL url = new URL(strUrl);
		TeamCityUrlBuilder urlBuilder = Mockito.mock(TeamCityUrlBuilder.class);
		when(urlBuilder.getVersion()).thenReturn(url.toString());

		TeamCityPlugin plugin = new TeamCityPlugin();
		plugin.urlBuilder = urlBuilder;

		SoftwareId softwareId = plugin.getSoftwareId(url);

		assertEquals("TeamCity", softwareId.getName());
		assertEquals("6.5", softwareId.getVersion());
		assertNull(softwareId.getWarnings());
	}

	@Ignore
	@Test(expected = IncompatibleSoftwareException.class)
	public void should_not_be_manageable() throws Exception {
		URL url = ClasspathFiles.getUrl("simple-text-file.txt");
		TeamCityUrlBuilder urlBuilder = Mockito.mock(TeamCityUrlBuilder.class);
		when(urlBuilder.getVersion()).thenReturn(url.toString());

		TeamCityPlugin plugin = new TeamCityPlugin();
		plugin.urlBuilder = urlBuilder;

		plugin.getSoftwareId(url);
	}

}
