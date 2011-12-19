package net.awired.visuwall.plugin.cruisecontrol;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class CruiseControlPluginActivator implements BundleActivator {

    private CruiseControlPlugin cruiseControlPlugin;
    private ServiceRegistration registration;

    public CruiseControlPluginActivator() {
        cruiseControlPlugin = new CruiseControlPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(VisuwallPlugin.class.getName(), cruiseControlPlugin, new Properties());
        System.out.println("CruiseControl plugin successfully loaded.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        cruiseControlPlugin = null;
        System.out.println("CruiseControl plugin successfully unloaded.");
    }
}
