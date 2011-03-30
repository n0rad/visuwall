package net.awired.visuwall.server.it.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.UUID;

import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;
import net.awired.visuwall.server.IntegrationTestData;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;
import net.awired.visuwall.server.exception.SoftwareNotCreatedException;
import net.awired.visuwall.server.exception.SoftwareNotFoundException;
import net.awired.visuwall.server.exception.WallNotFoundException;
import net.awired.visuwall.server.service.SoftwareService;
import net.awired.visuwall.server.service.WallService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/META-INF/spring/*-context.xml"})
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
        softwareService.persist(new Software("softwarehudson", HudsonPlugin.class, true, false));
        softwareService.persist(new Software("softwaresonar", SonarPlugin.class, false, true));

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
