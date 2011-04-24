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

package net.awired.visuwall;

import static org.junit.Assert.assertFalse;

import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.core.exception.NotFoundException;
import net.awired.visuwall.core.service.WallHolderService;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations={"classpath*:/META-INF/spring/*-context.xml"})
public class WholeTestIT {

    @Autowired
    WallHolderService wallService;

    @Ignore
    @Test
    public void classic_test() throws NotCreatedException, NotFoundException {
        Wall newwall = new Wall("standard wall");
        List<SoftwareAccess> softwareAccesses = newwall.getSoftwareAccesses();
        softwareAccesses.add(IntegrationTestData.HUDSON_ACCESS);
        softwareAccesses.add(IntegrationTestData.SONAR_ACCESS);

        wallService.persist(newwall);

        Wall wall = wallService.find("standard wall");

        List<Project> projects = wall.getProjects();

        assertFalse(projects.isEmpty());
    }
}
