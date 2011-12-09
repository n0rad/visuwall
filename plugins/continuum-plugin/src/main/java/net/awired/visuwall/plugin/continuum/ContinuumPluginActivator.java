package net.awired.visuwall.plugin.continuum;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class ContinuumPluginActivator implements BundleActivator {

    private ContinuumPlugin continuumPlugin;
    private ServiceRegistration registration;

    public ContinuumPluginActivator() {
        continuumPlugin = new ContinuumPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        System.out.println("Continuum plugin loaded.");
        registration = context.registerService(VisuwallPlugin.class.getName(), continuumPlugin, new Properties());
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        continuumPlugin = null;
    }
}
