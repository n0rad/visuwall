package net.awired.visuwall.core.business.service;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.core.application.KarafMain;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import net.awired.visuwall.core.business.domain.CapabilityEnum;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;
import org.apache.felix.framework.util.FelixConstants;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class KarafOsgiService implements PluginServiceInterface {

    private static final Logger LOG = LoggerFactory.getLogger(KarafOsgiService.class);

    @Autowired
    ApplicationContext context;

    private KarafMain main;
    private Framework osgi;
    HostActivator m_activator;
    private ServiceTracker m_tracker;

    @PostConstruct
    void postconstruct() {
        String home = System.getProperty(ApplicationHelper.HOME_KEY) + "/karaf";
        new File(home + "/data").mkdirs();
        new File(home + "/deploy").mkdirs();

        try {
            System.setProperty(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
                    "net.awired.visuwall.api.plugin; version=0.3.0.SNAPSHOT,"
                            + "net.awired.visuwall.api.domain; version=0.3.0.SNAPSHOT,"
                            + "net.awired.visuwall.api.exception; version=0.3.0.SNAPSHOT,"
                            + "net.awired.visuwall.api.plugin.tck; version=0.3.0.SNAPSHOT,"
                            + "net.awired.visuwall.api.plugin.capability; version=0.3.0.SNAPSHOT,"
                            + "net.awired.visuwall.api.domain.quality; version=0.3.0.SNAPSHOT");

            Resource resource = context.getResource("/WEB-INF/karaf");
            if (resource == null || !resource.exists()) {
                throw new RuntimeException("Karaf root folder not found : " + resource);
            }

            String root;
            try {
                File file = resource.getFile();
                root = file.getAbsolutePath();
            } catch (IOException e) {
                throw new RuntimeException("Karaf root folder : '" + resource
                        + "' is not a file, non unpacked wars servlet containers is currently not supported");
            }

            home = root;
            System.setProperty("karaf.home", root);
            System.setProperty("karaf.base", home);
            System.setProperty("karaf.data", home + "/data");
            System.setProperty("karaf.history", home + "/data/history.txt");
            System.setProperty("karaf.instances", home + "/instances");
            System.setProperty("karaf.startLocalConsole", "false");
            System.setProperty("karaf.startRemoteShell", "true");
            System.setProperty("karaf.lock", "false");
            main = new KarafMain(new String[0]);

            m_activator = new HostActivator();
            List<HostActivator> list = new ArrayList<HostActivator>();
            list.add(m_activator);

            Properties p = new Properties();
            p.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);
            main.setAdditionalProperties(p);

            LOG.info("Starting Karaf with root directory : " + root);
            main.launch();
            osgi = main.getFramework();

            Thread.sleep(10000);
        } catch (Exception e) {
            throw new RuntimeException("Cannot start osgi container", e);
        }

        m_tracker = new ServiceTracker(m_activator.getContext(), VisuwallPlugin.class.getName(), null);
        m_tracker.open();
    }

    @PreDestroy
    void destroy() {
        try {
            LOG.info("shutdown osgi container");
            if (main != null) {
                main.destroy();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public VisuwallPlugin<BasicCapability> getPluginFromUrl(URL url) {
        Object[] services = m_tracker.getServices();
        for (int i = 0; (services != null) && (i < services.length); i++) {
            VisuwallPlugin<BasicCapability> visuwallPlugin = (VisuwallPlugin<BasicCapability>) services[i];
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

    @Override
    public SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties) {
        Object[] services = m_tracker.getServices();
        for (int i = 0; (services != null) && (i < services.length); i++) {
            VisuwallPlugin<BasicCapability> visuwallPlugin = (VisuwallPlugin<BasicCapability>) services[i];
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
                BasicCapability connectionPlugin = visuwallPlugin.getConnection(url.toString(), properties);
                softwareInfo.setProjectNames(connectionPlugin.listSoftwareProjectIds());
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

    @Override
    public List<VisuwallPlugin<BasicCapability>> getPlugins() {
        @SuppressWarnings("rawtypes")
        Object[] services = m_tracker.getServices();
        List<VisuwallPlugin<BasicCapability>> result = new ArrayList<VisuwallPlugin<BasicCapability>>();
        for (int i = 0; (services != null) && (i < services.length); i++) {
            VisuwallPlugin<BasicCapability> visuwallPlugin = (VisuwallPlugin<BasicCapability>) services[i];
            result.add(visuwallPlugin);
        }
        return result;
    }

}
