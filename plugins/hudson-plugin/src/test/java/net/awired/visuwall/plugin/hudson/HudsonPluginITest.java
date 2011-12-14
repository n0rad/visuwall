package net.awired.visuwall.plugin.hudson;

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
        SoftwareProjectId projectId = new SoftwareProjectId("struts 2 instable");
        System.out.println(connection.isBuilding(projectId, "5"));
        System.out.println(connection.isBuilding(projectId, "4"));

    }

}
