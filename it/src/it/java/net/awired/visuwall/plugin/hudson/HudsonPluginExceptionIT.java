package net.awired.visuwall.plugin.hudson;

import static net.awired.visuwall.IntegrationTestData.HUDSON_ID;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
<<<<<<< HEAD:hudson-plugin/src/test/java/net/awired/visuwall/plugin/hudson/it/service/HudsonServiceExceptionIT.java
import net.awired.visuwall.plugin.hudson.HudsonConnectionPlugin;
import net.awired.visuwall.plugin.hudson.it.IntegrationTestData;
=======
>>>>>>> d1e18665bbedc8f7ed1fe0a53acb4e91bb188f97:it/src/it/java/net/awired/visuwall/plugin/hudson/HudsonPluginExceptionIT.java

import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonPluginExceptionIT {

<<<<<<< HEAD:hudson-plugin/src/test/java/net/awired/visuwall/plugin/hudson/it/service/HudsonServiceExceptionIT.java
    static HudsonConnectionPlugin hudsonService = new HudsonConnectionPlugin();
=======
    static HudsonPlugin hudsonPlugin = new HudsonPlugin();
>>>>>>> d1e18665bbedc8f7ed1fe0a53acb4e91bb188f97:it/src/it/java/net/awired/visuwall/plugin/hudson/HudsonPluginExceptionIT.java

    @BeforeClass
    public static void init() {
        hudsonPlugin.setUrl(IntegrationTestData.HUDSON_URL);
        hudsonPlugin.init();
    }

    @Test(expected=BuildNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_build() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonPlugin.findBuildByBuildNumber(projectId, 0);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonPlugin.findProject(projectId);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_estimated_finish_time_of_inexistant_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonPlugin.getEstimatedFinishTime(projectId);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_last_build_number_of_inexistant_project() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonPlugin.getLastBuildNumber(projectId);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_is_building_of_inexistant_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonPlugin.isBuilding(projectId);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_populating_inexistant_project() throws ProjectNotFoundException {
        Project project = new Project();
        project.setName("");
        hudsonPlugin.populate(project);
    }
}
