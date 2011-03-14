package com.jsmadja.wall.projectwall.service;

import org.junit.Test;

import com.jsmadja.wall.projectwall.domain.Wall;


public class WallServiceTest {

    @Test (expected = NullPointerException.class)
    public void should_not_accept_null_parameter() {
        WallService wallService = new WallService();
        wallService.addWall(null);
    }

    @Test
    public void should_add_a_wall() {
        Wall wall = new Wall("mywall");
        WallService wallService = new WallService();
        wallService.addWall(wall);
    }

    @Test
    public void should_refresh_when_no_wall() {
        WallService wallService = new WallService();
        wallService.refreshWalls();
    }

    @Test
    public void should_refresh_when_one_wall() {
        WallService wallService = new WallService();
        Wall wall = new Wall("mywall");
        wallService.addWall(wall);
        wallService.refreshWalls();
    }
}
