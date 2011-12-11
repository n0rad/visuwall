package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertTrue;
import java.net.URL;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import org.junit.Test;

public class HudsonPluginITest {

    final HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @Test
    public void should_check_plugin_management() throws Exception {
        URL hudsonUrl = new URL("http://ci.visuwall.awired.net/");
        HudsonConnection connection = hudsonPlugin.getConnection(hudsonUrl,
                hudsonPlugin.getPropertiesWithDefaultValue());
        SoftwareProjectId projectId = new SoftwareProjectId("disabled");
        assertTrue(connection.isProjectDisabled(projectId));
    }

}
