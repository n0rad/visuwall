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
package fr.norad.visuwall.core.business.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;
import fr.norad.visuwall.api.plugin.VisuwallPlugin;
import fr.norad.visuwall.api.plugin.capability.BasicCapability;
import fr.norad.visuwall.api.plugin.capability.ViewCapability;
import fr.norad.visuwall.core.business.domain.CapabilityEnum;
import fr.norad.visuwall.core.business.domain.PluginInfo;
import fr.norad.visuwall.core.business.domain.SoftwareInfo;

@Service
public class VisuwallSpiService implements PluginServiceInterface {

    private static final Logger LOG = LoggerFactory.getLogger(VisuwallSpiService.class);

    @SuppressWarnings("rawtypes")
    private ServiceLoader<VisuwallPlugin> pluginLoader = ServiceLoader.load(VisuwallPlugin.class);

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

    @Override
    public BasicCapability getPluginConnectionFromUrl(URL url, Map<String, String> properties) {
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : getPlugins()) {
            try {
                visuwallPlugin.getSoftwareId(url, properties);
                return visuwallPlugin.getConnection(url, properties);
            } catch (SoftwareNotFoundException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info("Plugin " + visuwallPlugin + " can't manage url " + url);
                }
            } catch (Throwable e) {
                LOG.warn("Plugin " + visuwallPlugin + " throws exception on url " + url, e);
            }
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    @Override
    public SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties) {
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : getPlugins()) {
            SoftwareId softwareId = null;
            BasicCapability connectionPlugin = null;
            try {
                softwareId = visuwallPlugin.getSoftwareId(url, properties);
                Preconditions.checkNotNull(softwareId, "isManageable() should not return null", visuwallPlugin);

                SoftwareInfo softwareInfo = new SoftwareInfo();
                softwareInfo.setSoftwareId(softwareId);
                softwareInfo.setPluginInfo(getPluginInfo(visuwallPlugin));

                connectionPlugin = visuwallPlugin.getConnection(url, properties);

                softwareInfo.setProjectNames(connectionPlugin.listSoftwareProjectIds());
                if (connectionPlugin instanceof ViewCapability) {
                    softwareInfo.setViewNames(((ViewCapability) connectionPlugin).findViews());
                }
                return softwareInfo;
            } catch (SoftwareNotFoundException e) {
                LOG.debug("Plugin " + visuwallPlugin + " can not manage url " + url);
            } catch (Throwable e) {
                LOG.warn("Plugin " + visuwallPlugin + " throws exception on url " + url, e);
            } finally {
                if (connectionPlugin != null) {
                    connectionPlugin.close();
                }
            }
        }
        throw new RuntimeException("no plugin to manage url " + url);
    }

    @Override
    public PluginInfo getPluginInfo(VisuwallPlugin<BasicCapability> visuwallPlugin) {
        PluginInfo pluginInfo = new PluginInfo();
        pluginInfo.setName(visuwallPlugin.getName());
        pluginInfo.setVersion(visuwallPlugin.getVersion());
        pluginInfo.setProperties(visuwallPlugin.getPropertiesWithDefaultValue());
        Class<BasicCapability> connectionClass = visuwallPlugin.getConnectionClass();
        pluginInfo.setCapabilities(CapabilityEnum.getCapabilitiesForClass(connectionClass));
        return pluginInfo;
    }

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

}
