/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.core.service;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import net.awired.visuwall.core.domain.ConnectedProject;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.persistence.dao.WallDAO;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class WallHolderServiceTest {

    private WallHolderService wallHolderService;

    @Before
    public void init() throws NotCreatedException {
        List<Wall> walls = new ArrayList<Wall>();

        Query query = Mockito.mock(Query.class);
        when(query.getResultList()).thenReturn(walls);

        // EntityManager entityManager = Mockito.mock(EntityManager.class);
        // when(entityManager.createNamedQuery(Matchers.anyString())).thenReturn(query);

        WallDAO wallService = Mockito.mock(WallDAO.class);
        BuildProjectService projectService = Mockito.mock(BuildProjectService.class);
        PluginService pluginService = Mockito.mock(PluginService.class);

        wallHolderService = new WallHolderService();
        wallHolderService.wallDAO = wallService;
        wallHolderService.init();
    }

    @After
    public void after() {
        wallHolderService.WALLS.clear();
    }

    @Test(expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws NotCreatedException {
        wallHolderService.update(null);
    }

    @Test
    public void should_add_a_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        wallHolderService.update(wall);
    }

    @Test
    public void should_find_a_wall() throws NotFoundException, NotCreatedException {
        String wallName = "wallName";
        wallHolderService.update(new Wall(wallName));
        Wall wall = wallHolderService.find(wallName);

        assertNotNull(wall);
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_wall() throws NotFoundException {
        wallHolderService.find("not.exist");
    }

    @Test
    public void should_find_wall_names() throws NotCreatedException {
        wallHolderService.update(new Wall("wall1"));
        wallHolderService.update(new Wall("wall2"));

        Set<String> wallNames = wallHolderService.getWallNames();
        assertTrue(wallNames.contains("wall1"));
        assertTrue(wallNames.contains("wall2"));
    }

    @Ignore
    @Test
    public void should_find_status() throws NotFoundException, NotCreatedException {
        Wall wall2 = new Wall("wall1");
        wallHolderService.update(wall2);

        Wall wall = wallHolderService.find("wall1");
        ConnectedProject project = new ConnectedProject("test");
        wall.getProjects().add(project);

        //        List<ProjectStatus> status = wallHolderService.getStatus("wall1");
        //        assertFalse(status.isEmpty());
    }

    @Test
    public void should_return_walls() throws NotCreatedException {
        assertTrue(wallHolderService.getWalls().isEmpty());

        wallHolderService.update(new Wall("wall"));

        assertFalse(wallHolderService.getWalls().isEmpty());
    }
}
