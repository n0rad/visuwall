package net.awired.visuwall.plugin.bamboo;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class BambooPluginActivator implements BundleActivator {

    private BambooPlugin BambooPlugin;
    private ServiceRegistration registration;

    public BambooPluginActivator() {
        BambooPlugin = new BambooPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(VisuwallPlugin.class.getName(), BambooPlugin, new Properties());
        System.out.println("Bamboo plugin successfully loaded.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        BambooPlugin = null;
        System.out.println("Bamboo plugin successfully unloaded.");
    }
}
