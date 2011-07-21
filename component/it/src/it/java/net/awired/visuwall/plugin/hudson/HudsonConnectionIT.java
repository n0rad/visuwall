package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import java.util.List;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonConnectionIT {

    static HudsonConnection hudsonConnection;

    @BeforeClass
    public static void init() {
        hudsonConnection = new HudsonConnection();
        hudsonConnection.connect("http://fluxx.fr.cr:8080/hudson");
    }

    @Test
    public void should_find_freestyle_project() {
        List<String> names = hudsonConnection.findProjectNames();
        assertTrue(names.contains("client-teamcity"));
    }

    @Test
    public void should_find_name_of_freestyle_project() throws ProjectNotFoundException {
        SoftwareProjectId softwareProjectId = new SoftwareProjectId("client-teamcity");
        String name = hudsonConnection.getName(softwareProjectId);
        assertEquals("client-teamcity", name);
    }
}
