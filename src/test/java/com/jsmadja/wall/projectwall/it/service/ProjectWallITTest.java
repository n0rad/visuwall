/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
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

import java.util.List;

import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.service.ProjectWallService;

public class ProjectWallITTest {

    ProjectWallService projectWall = new ProjectWallService(Integration.HUDSON_URL, Integration.SONAR_URL);

    @Test
    public void should_retrieve_all_data() {
        List<Project> projects = projectWall.getProjects();
        for (Project project:projects) {
            System.err.println(project);
        }
    }
}
