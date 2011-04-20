package net.awired.visuwall.core.service;

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
import net.awired.visuwall.core.service.WallHolderService;
import net.awired.visuwall.core.service.WallService;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class WallHolderServiceTest {

    private WallHolderService wallHolderService;

    @Before
    public void init() throws NotCreatedException {
        List<Wall> walls = new ArrayList<Wall>();

        Query query = Mockito.mock(Query.class);
        when(query.getResultList()).thenReturn(walls);

//        EntityManager entityManager = Mockito.mock(EntityManager.class);
//        when(entityManager.createNamedQuery(Matchers.anyString())).thenReturn(query);

        WallService wallService = Mockito.mock(WallService.class);
        ProjectService projectService = Mockito.mock(ProjectService.class);
        PluginService pluginService = Mockito.mock(PluginService.class);

        wallHolderService = new WallHolderService();
        wallHolderService.wallService = wallService;
        wallHolderService.projectService = projectService;
        wallHolderService.pluginService = pluginService;
    }

    @After
    public void after() {
    	wallHolderService.WALLS.clear();
    }
    
    
    @Test (expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws NotCreatedException {
        wallHolderService.persist(null);
    }

    @Test
    public void should_add_a_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        wallHolderService.persist(wall);
    }

    @Test
    public void should_refresh_when_no_wall() {
        wallHolderService.refreshWalls();
    }

    @Test
    public void should_refresh_when_one_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        wallHolderService.persist(wall);
        wallHolderService.refreshWalls();
    }

    @Test
    public void should_find_a_wall() throws NotFoundException, NotCreatedException {
        String wallName = "wallName";
        wallHolderService.persist(new Wall(wallName));
        Wall wall = wallHolderService.find(wallName);

        assertNotNull(wall);
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_wall() throws NotFoundException {
        wallHolderService.find("not.exist");
    }

    @Test
    public void should_find_wall_names() throws NotCreatedException {
        wallHolderService.persist(new Wall("wall1"));
        wallHolderService.persist(new Wall("wall2"));

        Set<String> wallNames = wallHolderService.getWallNames();
        assertTrue(wallNames.contains("wall1"));
        assertTrue(wallNames.contains("wall2"));
    }

    @Test
    public void should_find_status() throws NotFoundException, NotCreatedException {
        Wall wall2 = new Wall("wall1");
        wallHolderService.persist(wall2);
    	
    	Wall wall = wallHolderService.find("wall1");
        Project project = new Project();
        ProjectId projectId = new ProjectId();
        project.setProjectId(projectId);
        wall.getProjects().add(project);

        List<ProjectStatus> status = wallHolderService.getStatus("wall1");
        assertFalse(status.isEmpty());
    }

    @Test
    public void should_return_walls() throws NotCreatedException {
        assertTrue(wallHolderService.getWalls().isEmpty());

        wallHolderService.persist(new Wall("wall"));

        assertFalse(wallHolderService.getWalls().isEmpty());
    }
}
