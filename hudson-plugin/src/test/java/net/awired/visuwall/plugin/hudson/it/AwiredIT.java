package net.awired.visuwall.plugin.hudson.it;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;

import org.junit.BeforeClass;
import org.junit.Test;

public class AwiredIT {

    static HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @BeforeClass
    public static void init() {
        hudsonPlugin.setUrl("http://ci.awired.net/jenkins");
        hudsonPlugin.init();
    }

    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = hudsonPlugin.findAllProjects();
        assertEquals(4, projects.size());
    }
}
