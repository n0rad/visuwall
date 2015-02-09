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

package fr.norad.visuwall.core.business.service;

import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.plugin.capability.BasicCapability;
import fr.norad.visuwall.core.persistence.entity.Wall;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectServiceTest {

    ProjectService projectService;

    @Before
    public void init() {
        projectService = new ProjectService();
        //        ProjectAggregatorService projectEnhancerService = Mockito.mock(ProjectAggregatorService.class);
        //        projectService.projectEnhancerService = projectEnhancerService;
    }

    public List<BasicCapability> getConnectionPlugins() {
        List<BasicCapability> connectionPlugins = new ArrayList<BasicCapability>();
        BasicCapability connectionPlugin = Mockito.mock(BasicCapability.class);
        connectionPlugins.add(connectionPlugin);

        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        projectIds.add(new SoftwareProjectId("name1"));
        projectIds.add(new SoftwareProjectId("name2"));
        return connectionPlugins;
    }

    @Ignore
    @Test
    public void should_call_merge_for_plugins() {
        List<BasicCapability> connectionPlugins = getConnectionPlugins();
        Wall wall = new Wall();
        // ConnectedProject project = new ConnectedProject("test");
        //project.setCapabilities((List) connectionPlugins);

        //        Runnable updateProjectRunner = projectService.getUpdateProjectRunner(wall, project);
        //        updateProjectRunner.run();

        //        Mockito.verify(projectService.projectEnhancerService).enhanceWithBuildInformations(project,
        //                connectionPlugins.iterator().next());
        //        Mockito.verify(projectService.projectEnhancerService).enhanceWithQualityAnalysis(project,
        //                connectionPlugins.iterator().next(), projectService.metrics);
    }

}
