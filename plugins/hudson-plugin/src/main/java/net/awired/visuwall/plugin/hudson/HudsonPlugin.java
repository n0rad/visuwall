/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.plugin.hudson;

import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class HudsonPlugin implements VisuwallPlugin {

	@Override
	public ConnectionPlugin connect(String url, Properties info) {
		return new HudsonConnectionPlugin(url, null, null);
	}

	@Override
	public String getName() {
		return "Hudson";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean isManageable(URL url) {
		return true;
	}
}