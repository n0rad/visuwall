package net.awired.visuwall.plugin.hudson;

import static net.awired.visuwall.api.domain.State.DISABLED;
import static org.junit.Assert.assertEquals;

import java.net.URL;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;

import org.junit.Test;

public class HudsonPluginITest {

    final HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @Test
    public void should_check_plugin_management() throws Exception {
        URL hudsonUrl = new URL("http://ci.awired.net/jenkins/");
        HudsonConnection connection = hudsonPlugin.getConnection(hudsonUrl,
                hudsonPlugin.getPropertiesWithDefaultValue());
        SoftwareProjectId projectId = new SoftwareProjectId("test42");
        State buildState = connection.getBuildState(projectId, "1");
        assertEquals(DISABLED, buildState);
    }

}
