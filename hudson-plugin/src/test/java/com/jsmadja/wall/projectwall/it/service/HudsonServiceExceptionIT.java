package com.jsmadja.wall.projectwall.it.service;

import org.junit.BeforeClass;
import org.junit.Test;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.exception.BuildNotFoundException;
import com.jsmadja.wall.projectwall.exception.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.it.IntegrationTestData;
import com.jsmadja.wall.projectwall.service.HudsonService;

public class HudsonServiceExceptionIT {

    static HudsonService hudsonService = new HudsonService();

    @BeforeClass
    public static void init() {
        hudsonService.setUrl(IntegrationTestData.HUDSON_URL);
        hudsonService.init();
    }

    @Test(expected=BuildNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_build() throws BuildNotFoundException {
        hudsonService.findBuildByProjectNameAndBuildNumber("", 0);
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws ProjectNotFoundException {
        hudsonService.findProjectByName("");
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_estimated_finish_time_of_inexistant_project() throws ProjectNotFoundException {
        hudsonService.getEstimatedFinishTime("");
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_last_build_number_of_inexistant_project() throws BuildNotFoundException, ProjectNotFoundException {
        hudsonService.getLastBuildNumber("");
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_is_building_of_inexistant_project() throws ProjectNotFoundException {
        hudsonService.isBuilding("");
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_populating_inexistant_project() throws ProjectNotFoundException {
        Project project = new Project();
        project.setName("");
        hudsonService.populate(project);
    }
}
