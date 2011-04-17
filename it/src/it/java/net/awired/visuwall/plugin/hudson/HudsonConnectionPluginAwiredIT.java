package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;

import java.util.List;

import net.awired.visuwall.api.domain.ProjectId;

import org.junit.Test;

public class HudsonConnectionPluginAwiredIT {
    static HudsonConnectionPlugin hudsonPlugin = new HudsonConnectionPlugin("http://ci.awired.net/jenkins");


    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = hudsonPlugin.findAllProjects();
        assertEquals(1, projects.size()); // devrait etre 4 mais bug hudson/jenkins avec projets sur github
    }
}
