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
public class SoftwareServiceIT {

    //    @Autowired
    //    SoftwareService softwareService;
    //
    //    Logger LOG = LoggerFactory.getLogger(SoftwareServiceIT.class);
    //
    //    @Test
    //    public void should_find_all_software() throws NotCreatedException {
    //        Software software1 = new Software("software1", HudsonConnectionPlugin.class, true, true);
    //        Software software2 = new Software("software2", SonarConnectionPlugin.class, true, true);
    //        softwareService.persist(software1);
    //        softwareService.persist(software2);
    //
    //        List<Software> softwares = softwareService.findAll();
    //        assertTrue(softwares.contains(software1));
    //        assertTrue(softwares.contains(software2));
    //    }
    //
    //    @Test
    //    public void should_persist_software() throws SoftwareNotFoundException, NotCreatedException {
    //        String name = "hudson";
    //        String className = HudsonConnectionPlugin.class.getName();
    //        boolean buildSoftware = true;
    //        boolean qualitySoftware = false;
    //        Software software = new Software(name, HudsonConnectionPlugin.class, buildSoftware, qualitySoftware);
    //        softwareService.persist(software);
    //
    //        Software persistedSoftware = softwareService.find(name);
    //
    //        assertEquals(name, persistedSoftware.getName());
    //        assertEquals(className, persistedSoftware.getClassName());
    //        assertEquals(buildSoftware, persistedSoftware.isBuildSoftware());
    //        assertEquals(qualitySoftware, persistedSoftware.isQualitySoftware());
    //    }
    //
    //    @Test(expected = NullPointerException.class)
    //    public void should_throw_exception_when_passing_null_to_persist() throws NotCreatedException {
    //        softwareService.persist(null);
    //    }
    //
    //    @Test
    //    public void should_find_a_software() throws SoftwareNotFoundException, NotCreatedException {
    //        Software mysoftware = new Software("mysoftware", HudsonConnectionPlugin.class, true, true);
    //        softwareService.persist(mysoftware);
    //        softwareService.find("mysoftware");
    //    }
    //
    //    @Test(expected=NotFoundException.class)
    //    public void should_throw_exception_when_searching_an_inexistant_software() throws NotFoundException {
    //        softwareService.find("does.not.exist");
    //    }
    //
    //    @Test(expected=NullPointerException.class)
    //    public void should_throw_exception_when_passing_null()  throws SoftwareNotFoundException {
    //        softwareService.find(null);
    //    }
    //
    //    @Test(expected = Exception.class)
    //    public void should_throw_exception_when_trying_to_persist_already_existing_software() throws NotCreatedException {
    //        String name = UUID.randomUUID().toString();
    //        softwareService.persist(new Software(name, HudsonConnectionPlugin.class, true, true));
    //        softwareService.persist(new Software(name, HudsonConnectionPlugin.class, true, true));
    //    }
}
