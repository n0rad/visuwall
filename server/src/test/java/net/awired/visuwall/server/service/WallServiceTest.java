package net.awired.visuwall.server.service;

import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.NotCreatedException;

import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class WallServiceTest {

    @Test (expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws NotCreatedException {
        WallService wallService = new WallService();
        wallService.persist(null);
    }

    @Test
    public void should_add_a_wall() throws NotCreatedException {
        Wall wall = new Wall("mywall");
        WallService wallService = new WallService();
        wallService.persist(wall);
    }

    @Test
    public void should_refresh_when_no_wall() {
        WallService wallService = new WallService();
        wallService.refreshWalls();
    }

    @Test
    public void should_refresh_when_one_wall() throws NotCreatedException {
        WallService wallService = new WallService();
        Wall wall = new Wall("mywall");
        wallService.persist(wall);
        wallService.refreshWalls();
    }

}
