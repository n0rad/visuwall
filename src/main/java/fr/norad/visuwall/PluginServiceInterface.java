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
package fr.norad.visuwall;

import java.net.URL;
import java.util.List;
import java.util.Map;
import fr.norad.visuwall.domain.PluginInfo;
import fr.norad.visuwall.domain.SoftwareInfo;
import fr.norad.visuwall.plugin.VisuwallPlugin;
import fr.norad.visuwall.plugin.capability.BasicCapability;

public interface PluginServiceInterface {

    BasicCapability getPluginConnectionFromUrl(URL url, Map<String, String> properties);

    SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties);

    PluginInfo getPluginInfo(VisuwallPlugin<BasicCapability> visuwallPlugin);

    List<PluginInfo> getPluginsInfo();

    List<VisuwallPlugin<BasicCapability>> getPlugins();

}
