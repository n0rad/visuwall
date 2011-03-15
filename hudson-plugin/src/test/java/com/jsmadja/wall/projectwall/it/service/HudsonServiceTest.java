package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.exception.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.HudsonService;

public class HudsonServiceTest {

    static String hudsonAwirdUrl = "http://ci.jwall.awired.net/";

    static HudsonService hudsonService = new HudsonService();

    @BeforeClass
    public static void init() {
        hudsonService.setUrl(hudsonAwirdUrl);
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
    }

}
