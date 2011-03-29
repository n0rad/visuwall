package net.awired.visuwall.plugin.bamboo.it.service;

import static net.awired.visuwall.plugin.bamboo.it.IntegrationTestData.BAMBOO_URL;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.plugin.bamboo.service.BambooService;

import org.junit.BeforeClass;
import org.junit.Test;

public class BambooServiceIT {

    private static final String BAMBOO_ID = "BAMBOO_ID";

    static BambooService bambooService = new BambooService();

    @BeforeClass
    public static void init() {
        bambooService.setUrl(BAMBOO_URL);
        bambooService.init();
    }

    @Test
    public void should_find_all_projects() {
        List<ProjectId> projects = bambooService.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(BAMBOO_ID, "struts 2 instable");
        Project project = bambooService.findProject(projectId);
        assertNotNull(project);
    }
}
