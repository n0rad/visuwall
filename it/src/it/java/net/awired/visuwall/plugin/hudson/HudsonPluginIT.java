package net.awired.visuwall.plugin.hudson;

import static net.awired.visuwall.IntegrationTestData.HUDSON_ID;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;


public class HudsonPluginIT {

    static HudsonPlugin hudsonPlugin = new HudsonPlugin();

    @BeforeClass
    public static void init() {
        hudsonPlugin.setUrl(IntegrationTestData.HUDSON_URL);
        hudsonPlugin.init();
    }

    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = hudsonPlugin.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "neverbuild");
        Project project = hudsonPlugin.findProject(projectId);
        assertNotNull(project);
    }

    @Test
    public void should_find_build_by_name_and_build_number() throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        Build build = hudsonPlugin.findBuildByBuildNumber(projectId, 3);
        assertNotNull(build);
    }

    @Test
    public void should_find_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        int buildNumber = hudsonPlugin.getLastBuildNumber(projectId);
        assertEquals(3, buildNumber);
    }

    @Test
    public void should_verify_not_building_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        boolean building = hudsonPlugin.isBuilding(projectId);
        assertFalse(building);
    }

    @Test
    public void should_verify_state() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        State state = hudsonPlugin.getState(projectId);
        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_populate_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "struts");
        Project project = hudsonPlugin.findProject(projectId);
        hudsonPlugin.populate(project);
        assertEquals("struts", project.getName());
    }
}
