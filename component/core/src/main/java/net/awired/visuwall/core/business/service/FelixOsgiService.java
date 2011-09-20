package net.awired.visuwall.core.business.service;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.ViewCapability;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import net.awired.visuwall.core.business.domain.CapabilityEnum;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;
import net.awired.visuwall.core.exception.NotFoundException;

import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleException;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.google.common.base.Preconditions;

//@Service
public class FelixOsgiService implements PluginServiceInterface {

	private static final Logger LOG = LoggerFactory
			.getLogger(FelixOsgiService.class);

	@Autowired
	ApplicationContext context;

	private Framework osgi;
	HostActivator m_activator;
	private ServiceTracker m_tracker = null;

//	@PostConstruct
	public void launchOsgiContainer() {

		String bundleDir = System.getProperty(ApplicationHelper.HOME_KEY)
				+ "/bundle";
		String cacheDir = System.getProperty(ApplicationHelper.HOME_KEY)
				+ "/cache";

		Map<Object, Object> properties = new HashMap<Object, Object>();
		properties.put(Constants.FRAMEWORK_STORAGE, cacheDir);
		properties.put(AutoProcessor.AUTO_DEPLOY_DIR_PROPERY, bundleDir);
		properties.put(AutoProcessor.AUTO_DEPLOY_ACTION_PROPERY,
				"install,start");

		m_activator = new HostActivator();
		List<HostActivator> list = new ArrayList<HostActivator>();
		list.add(m_activator);

		properties
				.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
								"net.awired.visuwall.api.plugin; version=0.3.0.SNAPSHOT,"
								+ "net.awired.visuwall.api.domain; version=0.3.0.SNAPSHOT,"
								+ "net.awired.visuwall.api.exception; version=0.3.0.SNAPSHOT,"
								+ "net.awired.visuwall.api.plugin.tck; version=0.3.0.SNAPSHOT,"
								+ "net.awired.visuwall.api.plugin.capability; version=0.3.0.SNAPSHOT,"
								+ "net.awired.visuwall.api.domain.quality; version=0.3.0.SNAPSHOT");

		properties.put(FelixConstants.SYSTEMBUNDLE_ACTIVATORS_PROP, list);

		// (7) Add a shutdown hook to clean stop the framework.
		String enableHook = "true";
		if ((enableHook == null) || !enableHook.equalsIgnoreCase("false")) {
			Runtime.getRuntime().addShutdownHook(
					new Thread("Felix Shutdown Hook") {
						public void run() {
							try {
								if (osgi != null) {
									osgi.stop();
									osgi.waitForStop(0);
								}
							} catch (Exception ex) {
								System.err.println("Error stopping framework: "
										+ ex);
							}
						}
					});
		}

		try {
			// (8) Create an instance and initialize the framework.
			FrameworkFactory factory = getFrameworkFactory();
			osgi = factory.newFramework(properties);
			osgi.init();
			// (9) Use the system bundle context to process the auto-deploy
			// and auto-install/auto-start properties.
			AutoProcessor.process(properties, osgi.getBundleContext());
			// (10) Start the framework.
			osgi.start();
		} catch (Exception ex) {
			System.err.println("Could not create framework: " + ex);
			ex.printStackTrace();
			System.exit(0);
		}

		Resource res = context.getResource("META-INF/bundle");
		try {
			File metaBundleDir = res.getFile();
			File[] listFiles = metaBundleDir.listFiles();
			for (File file : listFiles) {
				try {
					m_activator.getContext().installBundle(
							"file:" + file.getAbsolutePath());
				} catch (BundleException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		m_tracker = new ServiceTracker(m_activator.getContext(),
				VisuwallPlugin.class.getName(), null);
		m_tracker.open();
	}

	public VisuwallPlugin<?> getPlugin(String name) throws NotFoundException {
		Object[] services = m_tracker.getServices();
		for (int i = 0; (services != null) && (i < services.length); i++) {
			if (((VisuwallPlugin<?>) services[i]).getName().equals(name)) {
				return (VisuwallPlugin<?>) services[i];
			}
		}
		throw new NotFoundException(
				"VisuwallPlugin service not found in osgi engine for name : "
						+ name);
	}

	private FrameworkFactory getFrameworkFactory() throws Exception {
		URL url = this
				.getClass()
				.getClassLoader()
				.getResource(
						"META-INF/services/org.osgi.framework.launch.FrameworkFactory");
		if (url != null) {
			BufferedReader br = new BufferedReader(new InputStreamReader(
					url.openStream()));
			try {
				for (String s = br.readLine(); s != null; s = br.readLine()) {
					s = s.trim();
					// Try to load first non-empty, non-commented line.
					if ((s.length() > 0) && (s.charAt(0) != '#')) {
						return (FrameworkFactory) Class.forName(s)
								.newInstance();
					}
				}
			} finally {
				if (br != null)
					br.close();
			}
		}

		throw new Exception("Could not find framework factory.");
	}

	public Bundle[] getInstalledBundles() {
		// Use the system bundle activator to gain external
		// access to the set of installed bundles.
		return m_activator.getBundles();
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
					LOG.info("Plugin " + visuwallPlugin + " can't manage url "
							+ url);
				}
			} catch (Throwable e) {
				LOG.warn("Plugin " + visuwallPlugin
						+ " throws exception on url " + url, e);
			}
		}
		throw new RuntimeException("no plugin to manage url " + url);
	}

	@Override
	public SoftwareInfo getSoftwareInfoFromUrl(URL url) {
		Object[] services = m_tracker.getServices();
		for (int i = 0; (services != null) && (i < services.length); i++) {
			VisuwallPlugin<BasicCapability> visuwallPlugin = (VisuwallPlugin<BasicCapability>) services[i];
			SoftwareId softwareId = null;
			try {
				softwareId = visuwallPlugin.getSoftwareId(url);
				Preconditions
						.checkNotNull(softwareId,
								"isManageable() should not return null",
								visuwallPlugin);
			} catch (IncompatibleSoftwareException e) {
				LOG.debug("Plugin " + visuwallPlugin + " can not manage url "
						+ url);
				continue;
			} catch (Throwable e) {
				LOG.warn("Plugin " + visuwallPlugin
						+ " throws exception on url " + url, e);
				continue;
			}
			SoftwareInfo softwareInfo = new SoftwareInfo();
			softwareInfo.setSoftwareId(softwareId);
			softwareInfo.setPluginInfo(getPluginInfo(visuwallPlugin));
			// TODO change that null
			try {
				BasicCapability connectionPlugin = visuwallPlugin
						.getConnection(url.toString(), null);
				softwareInfo.setProjectNames(connectionPlugin
						.findProjectNames());
				if (connectionPlugin instanceof ViewCapability) {
					softwareInfo
							.setViewNames(((ViewCapability) connectionPlugin)
									.findViews());
				}
				return softwareInfo;
			} catch (ConnectionException e) {
				throw new RuntimeException("no plugin to manage url " + url, e);
			}
		}
		throw new RuntimeException("no plugin to manage url " + url);
	}

	@Override
	public PluginInfo getPluginInfo(
			VisuwallPlugin<BasicCapability> visuwallPlugin) {
		PluginInfo pluginInfo = new PluginInfo();
		pluginInfo.setName(visuwallPlugin.getName());
		pluginInfo.setVersion(visuwallPlugin.getVersion());
		Class<BasicCapability> connectionClass = visuwallPlugin
				.getConnectionClass();
		pluginInfo.setCapabilities(CapabilityEnum
				.getCapabilitiesForClass(connectionClass));
		return pluginInfo;
	}

	@Override
	public List<PluginInfo> getPluginsInfo() {
		List<VisuwallPlugin<BasicCapability>> visuwallPlugins = getPlugins();
		List<PluginInfo> pluginInfos = new ArrayList<PluginInfo>(
				visuwallPlugins.size());
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
