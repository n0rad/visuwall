/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.domain;

import static org.junit.Assert.assertEquals;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.persistence.entity.Wall;
import fr.norad.visuwall.plugin.capability.BuildCapability;

public class WallTest {

    @Ignore
    @Test(expected = ProjectNotFoundException.class)
    public void should_not_find_inexistant_project_by_name() throws ProjectNotFoundException {
        Wall wall = new Wall();
        wall.getProjects().getById("not.exist424242424242");
    }

    //TODO move to projectHolder
    @Test
    public void should_find_project_by_id() throws ProjectNotFoundException {
        SoftwareProjectId projectId = new SoftwareProjectId("name");
        BuildCapability buildCapability = Mockito.mock(BuildCapability.class);

        Wall wall = new Wall();

        Project project = new Project(projectId, buildCapability);
        wall.getProjects().add(project);

        Project foundProject = wall.getProjects().getById(project.getId());

        assertEquals(foundProject, project);
    }
}
