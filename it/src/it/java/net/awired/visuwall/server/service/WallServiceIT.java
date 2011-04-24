/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
