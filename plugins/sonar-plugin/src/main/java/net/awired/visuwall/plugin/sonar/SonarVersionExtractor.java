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

package net.awired.visuwall.plugin.sonar;

import net.awired.visuwall.plugin.sonar.resource.Properties;
import net.awired.visuwall.plugin.sonar.resource.Property;

import com.google.common.base.Preconditions;

public class SonarVersionExtractor {

	private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

	private Properties properties;

	public SonarVersionExtractor(Properties properties) {
		Preconditions.checkNotNull(properties, "properties is mandatory");
		this.properties = properties;
	}

	public String version() {
		for (Property property : properties.getProperties()) {
			if (property.isKey(SONAR_CORE_VERSION_KEY)) {
				return property.getValue();
			}
		}
		return "unknown";
	}

}
