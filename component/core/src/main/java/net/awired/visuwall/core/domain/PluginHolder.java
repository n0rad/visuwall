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

package net.awired.visuwall.core.domain;

import java.util.HashSet;
import java.util.Set;

import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;

import com.google.common.base.Objects;

public class PluginHolder {

	private final Set<BuildConnectionPlugin> buildServices = new HashSet<BuildConnectionPlugin>();

	private final Set<QualityConnectionPlugin> qualityServices = new HashSet<QualityConnectionPlugin>();

	public void addBuildService(BuildConnectionPlugin buildPlugin) {
		buildServices.add(buildPlugin);
	}

	public void addQualityService(QualityConnectionPlugin qualityPlugin) {
		qualityServices.add(qualityPlugin);
	}

	// //////////////////////////////////////////////////

	public Set<BuildConnectionPlugin> getBuildServices() {
		return buildServices;
	}

	public Set<QualityConnectionPlugin> getQualityServices() {
		return qualityServices;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
		        .add("buildServices", buildServices) //
		        .add("qualityServices", qualityServices) //
		        .toString();
	}
}
