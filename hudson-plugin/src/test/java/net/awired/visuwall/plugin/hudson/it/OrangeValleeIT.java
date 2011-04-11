package net.awired.visuwall.plugin.hudson.it;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;

import org.junit.BeforeClass;
import org.junit.Test;

public class OrangeValleeIT {

    static HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @BeforeClass
    public static void init() {
        hudsonPlugin.setUrl("http://10.2.40.60/lifeisbetteron/jenkins");
        hudsonPlugin.init();
    }

    @Test
    public void should_find_valid_artifact_id() throws ProjectNotFoundException {
        List<ProjectId> projects = hudsonPlugin.findAllProjects();

        System.err.println(Arrays.toString(projects.toArray()));

        ProjectId projectId = projects.get(6);
        System.err.println(projectId);

        Project project = hudsonPlugin.findProject(projectId);
        assertEquals(project.getProjectId().getArtifactId(), "com.orangevallee.on.server.wme:wme");
    }
}
