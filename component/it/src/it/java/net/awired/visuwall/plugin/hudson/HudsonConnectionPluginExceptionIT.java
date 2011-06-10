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

package net.awired.visuwall.plugin.hudson;

import static net.awired.visuwall.IntegrationTestData.HUDSON_ID;
import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.junit.BeforeClass;
import org.junit.Test;

public class HudsonConnectionPluginExceptionIT {

    static HudsonConnectionPlugin hudsonConnectionPlugin = new HudsonConnectionPlugin();

    @BeforeClass
    public static void setUp() {
        hudsonConnectionPlugin.connect(IntegrationTestData.HUDSON_URL);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_build() throws BuildNotFoundException,
            ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonConnectionPlugin.findBuildByBuildNumber(projectId, 0);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonConnectionPlugin.findProject(projectId);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_estimated_finish_time_of_inexistant_project()
            throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonConnectionPlugin.getEstimatedFinishTime(projectId);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_last_build_number_of_inexistant_project()
            throws BuildNotFoundException, ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonConnectionPlugin.getLastBuildNumber(projectId);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_searching_is_building_of_inexistant_project()
            throws ProjectNotFoundException {
        ProjectId projectId = new ProjectId();
        projectId.addId(HUDSON_ID, "");
        hudsonConnectionPlugin.isBuilding(projectId);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_populating_inexistant_project() throws ProjectNotFoundException {
        Project project = new Project("");
        hudsonConnectionPlugin.populate(project);
    }
}
