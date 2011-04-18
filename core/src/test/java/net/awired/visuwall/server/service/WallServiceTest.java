package net.awired.visuwall.server.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.persistence.EntityManager;
import javax.persistence.Query;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.service.PluginService;
import net.awired.visuwall.core.service.ProjectService;
import net.awired.visuwall.core.service.WallService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class WallServiceTest {

    private WallService wallService;

    @Before
    public void init() throws NotCreatedException {
        List<Wall> walls = new ArrayList<Wall>();

        Query query = Mockito.mock(Query.class);
        when(query.getResultList()).thenReturn(walls);

        EntityManager entityManager = Mockito.mock(EntityManager.class);
        when(entityManager.createNamedQuery(Matchers.anyString())).thenReturn(query);

        ProjectService projectService = Mockito.mock(ProjectService.class);

        PluginService pluginService = Mockito.mock(PluginService.class);

        wallService = new WallService();
        wallService.setEntityManager(entityManager);
        wallService.setProjectService(projectService);
        wallService.setPluginService(pluginService);
    }

    @Test (expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws NotCreatedException {
        wallService.persist(null);
    }

    @Test
    public void should_add_a_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        wallService.persist(wall);
    }

    @Test
    public void should_refresh_when_no_wall() {
        wallService.refreshWalls();
    }

    @Test
    public void should_refresh_when_one_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        wallService.persist(wall);
        wallService.refreshWalls();
    }

    @Test
    public void should_find_a_wall() throws NotFoundException, NotCreatedException {
        String wallName = "wallName";
        wallService.persist(new Wall(wallName));
        Wall wall = wallService.find(wallName);

        assertNotNull(wall);
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_wall() throws NotFoundException {
        wallService.find("not.exist");
    }

    @Test
    public void should_find_wall_names() throws NotCreatedException {
        wallService.persist(new Wall("wall1"));
        wallService.persist(new Wall("wall2"));

        Set<String> wallNames = wallService.getWallNames();
        assertTrue(wallNames.contains("wall1"));
        assertTrue(wallNames.contains("wall2"));
    }

    @Test
    public void should_find_status() throws NotFoundException {
        Wall wall = wallService.find("wall1");
        Project project = new Project();
        ProjectId projectId = new ProjectId();
        project.setProjectId(projectId);
        wall.getProjects().add(project);

        List<ProjectStatus> status = wallService.getStatus(wall);
        assertFalse(status.isEmpty());
    }

    @Test
    public void should_return_walls() throws NotCreatedException {
        assertTrue(wallService.getWalls().isEmpty());

        wallService.persist(new Wall("wall"));

        assertFalse(wallService.getWalls().isEmpty());
    }
}
