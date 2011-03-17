package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jsmadja.wall.projectwall.IntegrationTestData;
import com.jsmadja.wall.projectwall.domain.Software;
import com.jsmadja.wall.projectwall.domain.SoftwareAccess;
import com.jsmadja.wall.projectwall.domain.Wall;
import com.jsmadja.wall.projectwall.service.HudsonService;
import com.jsmadja.wall.projectwall.service.SoftwareNotCreatedException;
import com.jsmadja.wall.projectwall.service.SoftwareNotFoundException;
import com.jsmadja.wall.projectwall.service.SoftwareService;
import com.jsmadja.wall.projectwall.service.SonarService;
import com.jsmadja.wall.projectwall.service.WallNotFoundException;
import com.jsmadja.wall.projectwall.service.WallService;


@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"/META-INF/spring/root-context.xml"})
public class WallServiceITTest {

    @Autowired
    WallService wallService;

    @Autowired
    SoftwareService softwareService;

    @Test
    public void should_persist_wall() throws WallNotFoundException {
        String wallName = UUID.randomUUID().toString();
        Wall wall = new Wall(wallName);
        wallService.persist(wall);

        Wall persistedWall = wallService.load(wallName);

        assertEquals(wallName, persistedWall.getName());
        assertEquals(wall, persistedWall);
    }

    @Test(expected = WallNotFoundException.class)
    public void should_throw_exception_when_loading_inexistant_wall() throws WallNotFoundException {
        wallService.load("not_exist");
    }

    @Test
    public void should_persist_software_accesses() throws WallNotFoundException, SoftwareNotFoundException, SoftwareNotCreatedException {
        softwareService.persist(new Software("softwarehudson", HudsonService.class, true, false));
        softwareService.persist(new Software("softwaresonar", SonarService.class, false, true));

        String wallName = UUID.randomUUID().toString();
        Software hudson = softwareService.find("softwarehudson");
        Software sonar = softwareService.find("softwaresonar");

        SoftwareAccess hudsonAccess = new SoftwareAccess(hudson, IntegrationTestData.HUDSON_URL);
        SoftwareAccess sonarAccess = new SoftwareAccess(sonar, IntegrationTestData.SONAR_URL);

        Wall wall = new Wall(wallName);
        wall.addSoftwareAccess(hudsonAccess);
        wall.addSoftwareAccess(sonarAccess);

        wallService.persist(wall);

        Wall persistedWall = wallService.load(wallName);
        List<SoftwareAccess> softwareAccesses = persistedWall.getSoftwareAccesses();
        assertTrue(softwareAccesses.contains(hudsonAccess));
        assertTrue(softwareAccesses.contains(sonarAccess));
        assertFalse(persistedWall.getProjects().isEmpty());
    }

}
