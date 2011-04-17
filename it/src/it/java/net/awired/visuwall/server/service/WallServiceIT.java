package net.awired.visuwall.server.service;

import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;


@Ignore
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/META-INF/spring/*-context.xml"})
public class WallServiceIT {

    //    @Autowired
    //    WallService wallService;
    //
    //    @Autowired
    //    SoftwareService softwareService;
    //
    //    @Test
    //    public void should_persist_wall() throws NotFoundException {
    //        String wallName = UUID.randomUUID().toString();
    //        Wall wall = new Wall(wallName);
    //        wallService.persist(wall);
    //
    //        Wall persistedWall = wallService.load(wallName);
    //
    //        assertEquals(wallName, persistedWall.getName());
    //        assertEquals(wall, persistedWall);
    //    }
    //
    //    @Test(expected = NotFoundException.class)
    //    public void should_throw_exception_when_loading_inexistant_wall() throws NotFoundException {
    //        wallService.load("not_exist");
    //    }
    //
    //    @Test
    //    public void should_persist_software_accesses() throws NotFoundException, SoftwareNotFoundException, NotCreatedException {
    //        softwareService.persist(new Software("softwarehudson", HudsonConnectionPlugin.class, true, false));
    //        softwareService.persist(new Software("softwaresonar", SonarConnectionPlugin.class, false, true));
    //
    //        String wallName = UUID.randomUUID().toString();
    //        Software hudson = softwareService.find("softwarehudson");
    //        Software sonar = softwareService.find("softwaresonar");
    //
    //        SoftwareAccess hudsonAccess = new SoftwareAccess(hudson, IntegrationTestData.HUDSON_URL);
    //        SoftwareAccess sonarAccess = new SoftwareAccess(sonar, IntegrationTestData.SONAR_URL);
    //
    //        Wall wall = new Wall(wallName);
    //        wall.addSoftwareAccess(hudsonAccess);
    //        wall.addSoftwareAccess(sonarAccess);
    //
    //        wallService.persist(wall);
    //
    //        Wall persistedWall = wallService.load(wallName);
    //        List<SoftwareAccess> softwareAccesses = persistedWall.getSoftwareAccesses();
    //        assertTrue(softwareAccesses.contains(hudsonAccess));
    //        assertTrue(softwareAccesses.contains(sonarAccess));
    //        assertFalse(persistedWall.getProjects().isEmpty());
    //    }

}
