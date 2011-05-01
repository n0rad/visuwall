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

package net.awired.visuwall.core.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.core.domain.PluginHolder;
import net.awired.visuwall.core.domain.PluginInfo;
import net.awired.visuwall.core.domain.Software;
import net.awired.visuwall.core.domain.SoftwareAccess;

import org.springframework.stereotype.Service;

@Service
public class PluginService {

    public PluginService() {
    }

    private ServiceLoader<VisuwallPlugin> pluginLoader = ServiceLoader.load(VisuwallPlugin.class);

    public List<PluginInfo> getPluginsInfo() {
    	List<VisuwallPlugin> visuwallPlugins = getPlugins();
    	List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(visuwallPlugins.size());
    	for (VisuwallPlugin visuwallPlugin : visuwallPlugins) {
			PluginInfo pluginInfo = new PluginInfo();
			pluginInfo.setClassName(visuwallPlugin.getClass().getName());
			pluginInfo.setVersion(visuwallPlugin.getVersion());
			pluginInfo.setName(visuwallPlugin.getName());
			pluginInfos.add(pluginInfo);
		}
    	return pluginInfos;
    }
    
    public List<VisuwallPlugin> getPlugins() {
        Iterator<VisuwallPlugin> pluginIt = pluginLoader.iterator();
        List<VisuwallPlugin> result = new ArrayList<VisuwallPlugin>();
        while (pluginIt.hasNext()) {
            VisuwallPlugin plugin = pluginIt.next();
            result.add(plugin);
        }
        return result;
    }

    public void reload() {
        pluginLoader.reload();
    }

    public PluginHolder getPluginHolderFromSoftwares(List<SoftwareAccess> softwareAccesses) {
        PluginHolder pluginHolder = new PluginHolder();
        for (SoftwareAccess softwareAccess : softwareAccesses) {
            // TODO refactor as we recreate a connection
            VisuwallPlugin plugin = getPluginFromSoftware(softwareAccess);

            // connect
            Properties properties = new Properties();
            // properties.put("login", softwareAccess.getLogin());
            // properties.put("password", softwareAccess.getPassword());
            ConnectionPlugin connection = plugin.connect(softwareAccess.getUrl(), properties);

            if (connection instanceof BuildConnectionPlugin) {
                pluginHolder.addBuildService((BuildConnectionPlugin) connection);
            } else if (connection instanceof QualityConnectionPlugin) {
                pluginHolder.addQualityService((QualityConnectionPlugin) connection);
            } else {
                throw new RuntimeException("Unknow plugin connection type : " + connection);
            }
        }
        return pluginHolder;
    }

    public VisuwallPlugin getPluginFromSoftware(SoftwareAccess softwareAccess) {
        List<VisuwallPlugin> plugins = getPlugins();
        for (VisuwallPlugin plugin : plugins) {
            // TODO manage version
            if (softwareAccess.getPluginClassName().equals(plugin.getClass().getName())) {
                return plugin;
            }
        }
        throw new RuntimeException("plugin not found");
    }

}
