package com.jsmadja.wall.projectwall;

import java.util.List;

import org.junit.Test;

public class ProjectWallITTest {

    ProjectWall projectWall = new ProjectWall(Integration.HUDSON_URL, Integration.SONAR_URL);

    @Test
    public void should_retrieve_all_data() {
        List<Project> projects = projectWall.getProjects();
        for (Project project:projects) {
            System.err.println(project);
        }
    }
}
