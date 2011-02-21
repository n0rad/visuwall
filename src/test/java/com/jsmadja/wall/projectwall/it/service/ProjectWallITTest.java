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

import static org.junit.Assert.assertNotNull;

import java.util.List;

import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

public class ProjectWallITTest {

    ProjectWallService projectWall = new ProjectWallService();

    @Test
    public void should_retrieve_all_data() {
        List<Project> projects = projectWall.getProjects();
        for (Project project:projects) {
            System.err.println(project);
            assertNotNull(project.getHudsonJob());
        }
    }
}
