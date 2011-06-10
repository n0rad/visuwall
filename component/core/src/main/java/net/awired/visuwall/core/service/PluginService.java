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

package net.awired.visuwall.core.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;
import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.core.domain.SoftwareInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class PluginService {

    private static final Logger LOG = LoggerFactory.getLogger(PluginService.class);

    private ServiceLoader<VisuwallPlugin> pluginLoader = ServiceLoader.load(VisuwallPlugin.class);

    public VisuwallPlugin getPluginFromUrl(URL url) {
        List<VisuwallPlugin> visuwallPlugins = getPlugins();
        for (VisuwallPlugin visuwallPlugin : visuwallPlugins) {
            try {
                visuwallPlugin.getSoftwareId(url);
                return visuwallPlugin;
            } catch (IncompatibleSoftwareException e) {
                // TODO what do you want to log?
            }
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    public SoftwareInfo getSoftwareInfoFromUrl(URL url) {
        List<VisuwallPlugin> visuwallPlugins = getPlugins();
        for (VisuwallPlugin visuwallPlugin : visuwallPlugins) {
            SoftwareId softwareId = null;
            try {
                softwareId = visuwallPlugin.getSoftwareId(url);
                Preconditions.checkNotNull(softwareId, "isManageable() should not return null", visuwallPlugin);
            } catch (IncompatibleSoftwareException e) {
                LOG.debug("plugin [" + visuwallPlugin + "] is not compatible with software at URL : " + url);
                continue;
            }
            SoftwareInfo softwareInfo = new SoftwareInfo();
            softwareInfo.setSoftwareId(softwareId);
            softwareInfo.setPluginInfo(visuwallPlugin.getInfo());
            // TODO change that null
            ConnectionPlugin connectionPlugin = visuwallPlugin.getConnection(url.toString(), null);
            try {
                softwareInfo.setProjectNames(connectionPlugin.findProjectNames());
            } catch (NotImplementedOperationException e) {
                LOG.debug("plugin [" + visuwallPlugin + "] does not implement findProjectNames");
            }
            return softwareInfo;
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    public PluginInfo getPluginInfo(VisuwallPlugin visuwallPlugin) {
        PluginInfo pluginInfo = visuwallPlugin.getInfo();
        // TODO check value return from the plugin
        return pluginInfo;
    }

    public List<PluginInfo> getPluginsInfo() {
        List<VisuwallPlugin> visuwallPlugins = getPlugins();
        List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(visuwallPlugins.size());
        for (VisuwallPlugin visuwallPlugin : visuwallPlugins) {
            PluginInfo pluginInfo = getPluginInfo(visuwallPlugin);
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

    //    public List<ConnectionPlugin> getConnectionPluginsFromSoftwares(List<SoftwareAccess> softwareAccesses) {
    //        List<ConnectionPlugin> connectionPlugins = new ArrayList<ConnectionPlugin>(softwareAccesses.size());
    //        for (SoftwareAccess softwareAccess : softwareAccesses) {
    //            // TODO refactor as we recreate a connection
    //            VisuwallPlugin plugin = getPluginFromSoftware(softwareAccess);
    //
    //            // connect
    //            Properties properties = new Properties();
    //            // properties.put("login", softwareAccess.getLogin());
    //            // properties.put("password", softwareAccess.getPassword());
    //            ConnectionPlugin connection = plugin.getConnection(softwareAccess.getUrl(), properties);
    //            connectionPlugins.add(connection);
    //        }
    //        return connectionPlugins;
    //    }
    //
    //    public VisuwallPlugin getPluginFromSoftware(SoftwareAccess softwareAccess) {
    //        Preconditions.checkNotNull(softwareAccess.getPluginClassName(), "softwareAccess.getPluginClassName");
    //
    //        List<VisuwallPlugin> plugins = getPlugins();
    //        for (VisuwallPlugin plugin : plugins) {
    //            // TODO manage version
    //            if (softwareAccess.getPluginClassName().equals(plugin.getClass().getName())) {
    //                return plugin;
    //            }
    //        }
    //        throw new RuntimeException("plugin not found");
    //    }

}
