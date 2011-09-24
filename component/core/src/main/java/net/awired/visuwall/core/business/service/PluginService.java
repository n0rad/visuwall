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

package net.awired.visuwall.core.business.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.core.business.domain.CapabilityEnum;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

//@Service
@Deprecated
public class PluginService implements PluginServiceInterface {
	
    private static final Logger LOG = LoggerFactory.getLogger(PluginService.class);

    @SuppressWarnings("rawtypes")
    private ServiceLoader<VisuwallPlugin> pluginLoader = ServiceLoader.load(VisuwallPlugin.class);

    /* (non-Javadoc)
	 * @see net.awired.visuwall.core.business.service.PluginServiceInterface#getPluginFromUrl(java.net.URL)
	 */
    @Override
	public VisuwallPlugin<BasicCapability> getPluginFromUrl(URL url) {
        List<VisuwallPlugin<BasicCapability>> visuwallPlugins = getPlugins();
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : visuwallPlugins) {
            try {
                visuwallPlugin.getSoftwareId(url);
                return visuwallPlugin;
            } catch (IncompatibleSoftwareException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Plugin " + visuwallPlugin + " can't manage url " + url);
                }
            } catch (Throwable e) {
                LOG.warn("Plugin " + visuwallPlugin + " throws exception on url " + url, e);
            }
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    /* (non-Javadoc)
	 * @see net.awired.visuwall.core.business.service.PluginServiceInterface#getSoftwareInfoFromUrl(java.net.URL)
	 */
    @Override
	public SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties) {
        List<VisuwallPlugin<BasicCapability>> visuwallPlugins = getPlugins();
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : visuwallPlugins) {
            SoftwareId softwareId = null;
            try {
                softwareId = visuwallPlugin.getSoftwareId(url);
                Preconditions.checkNotNull(softwareId, "isManageable() should not return null", visuwallPlugin);
            } catch (IncompatibleSoftwareException e) {
                LOG.debug("Plugin " + visuwallPlugin + " can not manage url " + url);
                continue;
            } catch (Throwable e) {
                LOG.warn("Plugin " + visuwallPlugin + " throws exception on url " + url, e);
                continue;
            }
            SoftwareInfo softwareInfo = new SoftwareInfo();
            softwareInfo.setSoftwareId(softwareId);
            softwareInfo.setPluginInfo(getPluginInfo(visuwallPlugin));
            // TODO change that null
            try {
                BasicCapability connectionPlugin = visuwallPlugin.getConnection(url.toString(), null);
                softwareInfo.setProjectNames(connectionPlugin.findProjectNames());
                if (connectionPlugin instanceof ViewCapability) {
                    softwareInfo.setViewNames(((ViewCapability) connectionPlugin).findViews());
                }
                return softwareInfo;
            } catch (ConnectionException e) {
                throw new RuntimeException("no plugin to manage url " + url, e);
            }
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    /* (non-Javadoc)
	 * @see net.awired.visuwall.core.business.service.PluginServiceInterface#getPluginInfo(net.awired.visuwall.api.plugin.VisuwallPlugin)
	 */
    @Override
	public PluginInfo getPluginInfo(VisuwallPlugin<BasicCapability> visuwallPlugin) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName(visuwallPlugin.getName());
        pluginInfo.setVersion(visuwallPlugin.getVersion());
        Class<BasicCapability> connectionClass = visuwallPlugin.getConnectionClass();
        pluginInfo.setCapabilities(CapabilityEnum.getCapabilitiesForClass(connectionClass));
        return pluginInfo;
    }

    /* (non-Javadoc)
	 * @see net.awired.visuwall.core.business.service.PluginServiceInterface#getPluginsInfo()
	 */
    @Override
	public List<PluginInfo> getPluginsInfo() {
        List<VisuwallPlugin<BasicCapability>> visuwallPlugins = getPlugins();
        List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(visuwallPlugins.size());
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : visuwallPlugins) {
            PluginInfo pluginInfo = getPluginInfo(visuwallPlugin);
            pluginInfos.add(pluginInfo);
        }
        return pluginInfos;
    }

    /* (non-Javadoc)
	 * @see net.awired.visuwall.core.business.service.PluginServiceInterface#getPlugins()
	 */
    @Override
	public List<VisuwallPlugin<BasicCapability>> getPlugins() {
        @SuppressWarnings("rawtypes")
        Iterator<VisuwallPlugin> pluginIt = pluginLoader.iterator();
        List<VisuwallPlugin<BasicCapability>> result = new ArrayList<VisuwallPlugin<BasicCapability>>();
        while (pluginIt.hasNext()) {
            @SuppressWarnings("unchecked")
            VisuwallPlugin<BasicCapability> plugin = pluginIt.next();
            result.add(plugin);
        }
        return result;
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
