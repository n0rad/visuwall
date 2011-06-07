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

package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

import java.net.URL;

import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.domain.SoftwareInfo;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;

import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonPluginIT {
	
	static PluginInfo pluginInfo = new PluginInfo();

	@BeforeClass
	public static void init() {
		pluginInfo.setName("Hudson plugin");
		pluginInfo.setVersion(1.0f);
		pluginInfo.setClassName(HudsonPlugin.class.getName());
	}

	@Test
	public void should_recognize_hudson_instance_with_valid_url() throws Exception {
		HudsonPlugin hudsonPlugin = new HudsonPlugin();
		SoftwareInfo softwareInfo = hudsonPlugin.getSoftwareInfo(new URL("http://fluxx.fr.cr:8080/hudson"));

		assertEquals("Hudson", softwareInfo.getName());
		assertEquals(pluginInfo, softwareInfo.getPluginInfo());
		assertEquals("1.396", softwareInfo.getVersion());
		assertNull(softwareInfo.getWarnings());
	}

	@Test(expected = IncompatibleSoftwareException.class)
	public void should_not_fail_if_url_is_not_manageable() throws Exception {
		HudsonPlugin hudsonPlugin = new HudsonPlugin();
		String url = "http://www.google.fr";
		hudsonPlugin.getSoftwareInfo(new URL(url));
	}

}
