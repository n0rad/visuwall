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

package net.awired.visuwall.hudsonclient;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.when;
import net.awired.visuwall.hudsonclient.exception.ResourceNotFoundException;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;

import org.junit.Test;
import org.mockito.Mockito;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public class HudsonJerseyClientTest {

	@Test
	public void testGetSurefireReport() throws ResourceNotFoundException {
		HudsonJerseyClient hudsonJerseyClient = prepareClientFor(new HudsonMavenReportersSurefireAggregatedReport());
		HudsonMavenReportersSurefireAggregatedReport surefireReport = hudsonJerseyClient.getSurefireReport("url");
		assertNotNull(surefireReport);
	}

	@Test
	public void testGetModuleSetBuild() {
		HudsonJerseyClient hudsonJerseyClient = prepareClientFor(new HudsonMavenMavenModuleSetBuild());
		HudsonMavenMavenModuleSetBuild moduleSetBuild = hudsonJerseyClient.getModuleSetBuild("url");
		assertNotNull(moduleSetBuild);
	}

	@Test
	public void testGetModuleSet() {
		HudsonJerseyClient hudsonJerseyClient = prepareClientFor(new HudsonMavenMavenModuleSet());
		HudsonMavenMavenModuleSet moduleSet = hudsonJerseyClient.getModuleSet("url");
		assertNotNull(moduleSet);
	}

	@Test
	public void testGetHudsonJobs() {
		HudsonJerseyClient hudsonJerseyClient = prepareClientFor(new HudsonModelHudson());
		HudsonModelHudson hudsonModel = hudsonJerseyClient.getHudsonJobs("url");
		assertNotNull(hudsonModel);
	}

	@Test
	public void testGetHudsonUser() {
		HudsonJerseyClient hudsonJerseyClient = prepareClientFor(new HudsonUser());
		HudsonUser hudsonUser = hudsonJerseyClient.getHudsonUser("url");
		assertNotNull(hudsonUser);
	}

	private HudsonJerseyClient prepareClientFor(Object o) {
		WebResource resource = Mockito.mock(WebResource.class);
		when(resource.get(Mockito.any(Class.class))).thenReturn(o);

		Client client = Mockito.mock(Client.class);
		when(client.resource(Mockito.anyString())).thenReturn(resource);

		HudsonJerseyClient hudsonJerseyClient = new HudsonJerseyClient(client);
		return hudsonJerseyClient;
	}
}
