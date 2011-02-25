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

package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Collection;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.service.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration("/META-INF/spring/root-context.xml")
public class ProjectWallITTest {

    @Autowired
    private ProjectWallService projectWall;

    @Test
    @Ignore
    public void should_retrieve_all_data() {
        Collection<Project> projects = projectWall.findAllProjects();
        for (Project project:projects) {
            assertNotNull(project.getHudsonProject());
            System.err.println(project.getHudsonProject());
        }
    }

    @Test
    public void should_retrieve_data_of_one_project() throws ProjectNotFoundException {
        Project project = projectWall.findProject("fluxx");
        assertEquals("fluxx", project.getName());
    }

    @Test(expected=ProjectNotFoundException.class)
    public void should_throw_exception_when_search_non_existant_project() throws ProjectNotFoundException {
        projectWall.findProject("does.not.exist");
    }
}
