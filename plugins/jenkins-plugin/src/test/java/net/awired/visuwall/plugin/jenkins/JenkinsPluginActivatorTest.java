package net.awired.visuwall.plugin.jenkins;

import static org.mockito.Mockito.verify;

import java.util.Properties;

import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;

@RunWith(MockitoJUnitRunner.class)
public class JenkinsPluginActivatorTest {

    @InjectMocks
    JenkinsPluginActivator activator;

    @Mock
    JenkinsPlugin plugin;

    @Mock
    ServiceRegistration registration;

    @Mock
    BundleContext context;

    @Test
    public void should_start_activator() throws Exception {
        activator.start(context);
        verify(context).registerService(VisuwallPlugin.class.getName(), plugin, new Properties());
    }

    @Test
    public void should_stop_activator() throws Exception {
        activator.stop(context);
        verify(registration).unregister();
    }

}
