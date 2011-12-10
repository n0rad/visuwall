package net.awired.visuwall.plugin.hudson;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class HudsonPluginActivator implements BundleActivator {

    private HudsonPlugin hudsonPlugin;
    private ServiceRegistration registration;

    public HudsonPluginActivator() {
        hudsonPlugin = new HudsonPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(VisuwallPlugin.class.getName(), hudsonPlugin, new Properties());
        System.out.println("Hudson plugin successfully loaded.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        hudsonPlugin = null;
        System.out.println("Hudson plugin successfully unloaded.");
    }
}
