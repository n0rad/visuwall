package net.awired.visuwall.plugin.sonar;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class SonarPluginActivator implements BundleActivator {

    private SonarPlugin sonarPlugin;
    private ServiceRegistration registration;

    public SonarPluginActivator() {
        sonarPlugin = new SonarPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(VisuwallPlugin.class.getName(), sonarPlugin, new Properties());
        System.out.println("Sonar plugin successfully loaded.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        sonarPlugin = null;
        System.out.println("Sonar plugin successfully unloaded.");
    }
}
