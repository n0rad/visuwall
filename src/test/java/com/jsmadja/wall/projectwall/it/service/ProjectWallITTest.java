package com.jsmadja.wall.projectwall.it.service;

import java.util.List;

import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

public class ProjectWallITTest {

    ProjectWallService projectWall = new ProjectWallService(Integration.HUDSON_URL, Integration.SONAR_URL);

    @Test
    public void should_retrieve_all_data() {
        List<Project> projects = projectWall.getProjects();
        for (Project project:projects) {
            System.err.println(project);
        }
    }
}
