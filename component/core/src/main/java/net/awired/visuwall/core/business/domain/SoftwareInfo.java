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

package fr.norad.visuwall.core.business.domain;

import java.util.List;
import java.util.Map;

import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.domain.SoftwareProjectId;

public class SoftwareInfo {
    private SoftwareId softwareId;
    private PluginInfo pluginInfo;

    //TODO move to manage also quality
    private Map<SoftwareProjectId, String> projectNames;
    private List<String> viewNames;

    public PluginInfo getPluginInfo() {
        return pluginInfo;
    }

    public void setPluginInfo(PluginInfo pluginInfo) {
        this.pluginInfo = pluginInfo;
    }

    public List<String> getViewNames() {
        return viewNames;
    }

    public void setViewNames(List<String> viewNames) {
        this.viewNames = viewNames;
    }

    public SoftwareId getSoftwareId() {
        return softwareId;
    }

    public void setSoftwareId(SoftwareId softwareId) {
        this.softwareId = softwareId;
    }

	public void setProjectNames(Map<SoftwareProjectId, String> projectNames) {
		this.projectNames = projectNames;
	}

	public Map<SoftwareProjectId, String> getProjectNames() {
		return projectNames;
	}

}
