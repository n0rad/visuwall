package net.awired.visuwall.plugin.jenkins;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

public class JenkinsPluginActivator implements BundleActivator {

    private JenkinsPlugin jenkinsPlugin;
    private ServiceRegistration registration;

    public JenkinsPluginActivator() {
        jenkinsPlugin = new JenkinsPlugin();
    }

    @Override
    public void start(BundleContext context) throws Exception {
        registration = context.registerService(VisuwallPlugin.class.getName(), jenkinsPlugin, new Properties());
        System.out.println("Jenkins plugin successfully loaded.");
    }

    @Override
    public void stop(BundleContext context) throws Exception {
        registration.unregister();
        jenkinsPlugin = null;
        System.out.println("Jenkins plugin successfully unloaded.");
    }

}
