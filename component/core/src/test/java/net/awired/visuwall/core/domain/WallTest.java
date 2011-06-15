/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package net.awired.visuwall.core.domain;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.junit.Test;

public class WallTest {

    @Test(expected = ProjectNotFoundException.class)
    public void should_not_find_inexistant_project_by_project_id() throws ProjectNotFoundException {
        Wall wall = new Wall();
        ProjectId projectId = new ProjectId();
        wall.getProjectByProjectId(projectId);
    }

    @Test
    public void should_find_project_by_project_id() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setName("name");

        Wall wall = new Wall();
        ConnectedProject project = new ConnectedProject(projectId);
        wall.getProjects().add(project);

        Project foundProject = wall.getProjectByProjectId(projectId);

        assertEquals("name", foundProject.getName());
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_not_find_inexistant_project_by_name() throws ProjectNotFoundException {
        Wall wall = new Wall();
        wall.getProjectById("not.exist424242424242");
    }

    @Test
    public void should_find_project_by_id() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.setName("name");

        Wall wall = new Wall();
        ConnectedProject project = new ConnectedProject(projectId);
        wall.getProjects().add(project);

        Project foundProject = wall.getProjectById(project.getId());

        assertEquals(foundProject, project);
    }
}
