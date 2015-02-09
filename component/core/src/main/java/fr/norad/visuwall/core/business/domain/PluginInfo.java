/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.core.business.domain;

import java.util.List;
import java.util.Map;

import com.google.common.base.Objects;

public class PluginInfo {

    private String name;
    private float version;
    private Map<String, String> properties;
    private List<CapabilityEnum> capabilities;

    // //////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof PluginInfo) {
            PluginInfo pi = (PluginInfo) obj;
            return Objects.equal(name, pi.name) && //
                    Objects.equal(version, pi.version) //
            ;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, version);
    }

    public void setCapabilities(List<CapabilityEnum> capabilities) {
        this.capabilities = capabilities;
    }

    public List<CapabilityEnum> getCapabilities() {
        return capabilities;
    }

	public void setProperties(Map<String, String> properties) {
		this.properties = properties;
	}

	public Map<String, String> getProperties() {
		return properties;
	}
}
