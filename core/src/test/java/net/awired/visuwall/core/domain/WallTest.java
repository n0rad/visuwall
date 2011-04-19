package net.awired.visuwall.core.domain;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.Test;


public class WallTest {

    @Test(expected = ProjectNotFoundException.class)
    public void should_not_find_inexistant_project_by_project_id() throws ProjectNotFoundException {
        Wall wall = new Wall();
        ProjectId projectId = new ProjectId();
        wall.getProjectByProjectId(projectId);
    }

    @Test
    public void should_find_project_by_project_id() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setName("name");

        Wall wall = new Wall();
        Project project = new Project(projectId);
        wall.getProjects().add(project);

        Project foundProject = wall.getProjectByProjectId(projectId);

        assertEquals("name", foundProject.getName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_not_find_inexistant_project_by_name() throws ProjectNotFoundException {
        Wall wall = new Wall();
        wall.getProjectByName("not.exist");
    }

    @Test
    public void should_find_project_by_name() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setName("name");

        Wall wall = new Wall();
        Project project = new Project(projectId);
        wall.getProjects().add(project);

        Project foundProject = wall.getProjectByName("name");

        assertEquals("name", foundProject.getName());
    }
}
