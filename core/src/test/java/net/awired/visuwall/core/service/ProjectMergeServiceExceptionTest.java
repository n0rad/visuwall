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

package net.awired.visuwall.core.service;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.core.service.ProjectMergeService;

import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;


public class ProjectMergeServiceExceptionTest {

    ProjectMergeService projectMergeService = new ProjectMergeService();

    @Ignore
    @Test(expected = IllegalStateException.class)
    public void should_merge_one_project_with_one_build_plugin() {
        Project project = new Project("test");
        BuildConnectionPlugin buildPlugin = Mockito.mock(BuildConnectionPlugin.class);
        projectMergeService.merge(project, buildPlugin);
    }

}
