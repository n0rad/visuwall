package net.awired.visuwall.plugin.teamcity;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class TeamCityPluginActivator  implements BundleActivator {

	private TeamCityPlugin TeamCityPlugin;
	private ServiceRegistration registration;
	
	public TeamCityPluginActivator() {
		TeamCityPlugin = new TeamCityPlugin();
	}
	
	@Override
	public void start(BundleContext context) throws Exception {
		registration = context.registerService(VisuwallPlugin.class.getName(), TeamCityPlugin, new Properties());
	}

	@Override
	public void stop(BundleContext context) throws Exception {
		registration.unregister();
		TeamCityPlugin = null;
	}
}
