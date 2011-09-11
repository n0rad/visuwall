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

package net.awired.visuwall.server.application;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import javax.servlet.ServletContextEvent;

import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.core.application.common.ApplicationHelper;
import net.awired.visuwall.core.exception.NotFoundException;

import org.apache.felix.framework.util.FelixConstants;
import org.apache.felix.main.AutoProcessor;
import org.fusesource.jansi.AnsiConsole;
import org.osgi.framework.Bundle;
import org.osgi.framework.Constants;
import org.osgi.framework.launch.Framework;
import org.osgi.framework.launch.FrameworkFactory;
import org.osgi.util.tracker.ServiceTracker;
import org.springframework.web.context.ContextLoaderListener;

public class VisuwallContextLoaderListener extends ContextLoaderListener {

	private Framework osgi;
	HostActivator m_activator;
	private ServiceTracker m_tracker = null;

	@Override
	public void contextInitialized(ServletContextEvent event) {
		AnsiConsole.systemInstall();
		String home = ApplicationHelper.findHomeDir();
		System.setProperty(ApplicationHelper.HOME_KEY, home);

		ApplicationHelper.changeLogLvl();
		super.contextInitialized(event);
		launchOsgiContainer();
	}

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

		properties.put(Constants.FRAMEWORK_SYSTEMPACKAGES_EXTRA,
				"net.awired.visuwall.api.plugin.VisuwallPlugin; version=1.0.0");

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
		throw new NotFoundException("VisuwallPlugin service not found in osgi engine for name : " + name);
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

}
