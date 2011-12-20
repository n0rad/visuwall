package net.awired.visuwall.core.business.service;

import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.ServiceLoader;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.core.business.domain.CapabilityEnum;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class SpiPluginService implements PluginServiceInterface {

    private static final Logger LOG = LoggerFactory.getLogger(SpiPluginService.class);

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
                visuwallPlugin.getSoftwareId(url);
                return visuwallPlugin.getConnection(url, properties);
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

    @Override
    public SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties) {
        for (VisuwallPlugin<BasicCapability> visuwallPlugin : getPlugins()) {
            SoftwareId softwareId = null;
            BasicCapability connectionPlugin = null;
            try {
                softwareId = visuwallPlugin.getSoftwareId(url);
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
            } catch (IncompatibleSoftwareException e) {
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
