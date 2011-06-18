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

package net.awired.visuwall.core.business.service;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import javax.persistence.Query;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import net.awired.visuwall.core.business.process.WallProcess;
import net.awired.visuwall.core.business.service.WallHolderService;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.persistence.dao.WallDAO;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.junit.After;
import org.junit.Assert;
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
        WallProcess wallProcess = Mockito.mock(WallProcess.class);

        wallHolderService = new WallHolderService();
        wallHolderService.wallDAO = wallService;
        wallHolderService.wallProcess = wallProcess;
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
    public void should_add_a_wall() throws NotCreatedException, NotCreatedException, NotFoundException {
        WallDAO wallService = Mockito.mock(WallDAO.class);
        WallProcess wallProcess = Mockito.mock(WallProcess.class);
        Wall wall = new Wall("mywall");
        when(wallService.update(wall)).thenReturn(wall);

        wallHolderService = new WallHolderService();
        wallHolderService.wallDAO = wallService;
        wallHolderService.wallProcess = wallProcess;
        wallHolderService.init();

        wallHolderService.update(wall);
        Wall findWall = wallHolderService.find(wall.getName());
        Assert.assertEquals(wall, findWall);
    }

    @Test
    public void should_find_a_wall() throws NotFoundException, NotCreatedException {
        String wallName = "wallName";

        WallDAO wallService = Mockito.mock(WallDAO.class);
        WallProcess wallProcess = Mockito.mock(WallProcess.class);
        Wall wall = new Wall(wallName);
        when(wallService.update(wall)).thenReturn(wall);

        wallHolderService = new WallHolderService();
        wallHolderService.wallDAO = wallService;
        wallHolderService.wallProcess = wallProcess;
        wallHolderService.init();
        wallHolderService.update(wall);
        Wall findwall = wallHolderService.find(wallName);
        assertNotNull(findwall);
    }

    @Test(expected = NotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_wall() throws NotFoundException {
        wallHolderService.find("not.exist");
    }

    @Test
    public void should_find_wall_names() throws NotCreatedException {
        WallDAO wallService = Mockito.mock(WallDAO.class);
        WallProcess wallProcess = Mockito.mock(WallProcess.class);
        Wall wall1 = new Wall("wall1");
        Wall wall2 = new Wall("wall2");
        when(wallService.update(wall1)).thenReturn(wall1);
        when(wallService.update(wall2)).thenReturn(wall2);

        wallHolderService = new WallHolderService();
        wallHolderService.wallDAO = wallService;
        wallHolderService.wallProcess = wallProcess;
        wallHolderService.init();

        wallHolderService.update(wall1);
        wallHolderService.update(wall2);

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

}
