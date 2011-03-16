package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jsmadja.wall.projectwall.domain.Build;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.ProjectStatus.State;
import com.jsmadja.wall.projectwall.exception.BuildNotFoundException;
import com.jsmadja.wall.projectwall.exception.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.it.IntegrationTestData;
import com.jsmadja.wall.projectwall.service.HudsonService;

public class HudsonServiceIT {

    static HudsonService hudsonService = new HudsonService();

    @BeforeClass
    public static void init() {
        hudsonService.setUrl(IntegrationTestData.HUDSON_URL);
        hudsonService.init();
    }

    @Test
    public void should_find_all_projects() {
        List<Project> projects = hudsonService.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_project() throws ProjectNotFoundException {
        Project project = hudsonService.findProjectByName("neverbuild");
        assertNotNull(project);
    }

    @Test
    public void should_find_build_by_name_and_build_number() throws BuildNotFoundException {
        Build build = hudsonService.findBuildByProjectNameAndBuildNumber("struts", 3);
        assertNotNull(build);
    }

    @Test
    public void should_find_last_build_number() throws ProjectNotFoundException, BuildNotFoundException {
        int buildNumber = hudsonService.getLastBuildNumber("struts");
        assertEquals(3, buildNumber);
    }

    @Test
    public void should_verify_not_building_project() throws ProjectNotFoundException {
        boolean building = hudsonService.isBuilding("struts");
        assertFalse(building);
    }

    @Test
    public void should_verify_state() throws ProjectNotFoundException {
        State state = hudsonService.getState("struts");
        assertEquals(State.SUCCESS, state);
    }

    @Test
    public void should_populate_project() throws ProjectNotFoundException {
        Project project = hudsonService.findProjectByName("struts");
        System.err.println(project);
        hudsonService.populate(project);
        System.err.println(project);
        assertEquals("struts", project.getName());
    }
}
