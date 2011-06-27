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

package net.awired.visuwall.core.business.process.capabilities;

import java.util.Date;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.core.business.domain.Build;
import net.awired.visuwall.core.business.domain.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;

public class BuildCapabilityProcessTest {

    private BuildCapabilityProcess buildCapabilityProcess = new BuildCapabilityProcess();

    private static Project initProject(int previousBuildId, boolean previousBuilding, int newBuildId, boolean building)
            throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("name");
        BuildCapability buildCapability = Mockito.mock(BuildCapability.class);
        Mockito.when(buildCapability.getLastBuildNumber(projectId)).thenReturn(newBuildId);
        Mockito.when(buildCapability.isBuilding(projectId, newBuildId)).thenReturn(building);
        Project project = new Project(projectId, buildCapability);
        project.setLastBuildNumber(previousBuildId);
        Build previousBuild = project.findCreatedBuild(previousBuildId);
        previousBuild.setBuilding(previousBuilding);
        return project;
    }

    @Before
    public void initProcess() {
        buildCapabilityProcess = new BuildCapabilityProcess();
        TaskScheduler taskScheduler = Mockito.mock(TaskScheduler.class);
        buildCapabilityProcess.scheduler = taskScheduler;
    }

    @Test
    public void should_return_update_needed_when_new_build_already_done_in_software() throws Exception {
        Project project = initProject(42, false, 43, false);

        int[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new int[] { 43 }, buildIdToUpdate);
        Assert.assertFalse(project.getLastBuild().isBuilding());
        Assert.assertEquals(43, project.getLastBuildNumber());
        Mockito.verify(buildCapabilityProcess.scheduler, Mockito.times(0)).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_new_build_building_in_software() throws Exception {
        Project project = initProject(42, false, 43, true);

        int[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new int[] {}, buildIdToUpdate);
        Assert.assertTrue(project.getLastBuild().isBuilding());
        Assert.assertEquals(43, project.getLastBuildNumber());
        Mockito.verify(buildCapabilityProcess.scheduler).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_no_new_build_in_software() throws Exception {
        Project project = initProject(42, false, 42, false);

        int[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new int[] {}, buildIdToUpdate);
        Assert.assertFalse(project.getLastBuild().isBuilding());
        Assert.assertEquals(42, project.getLastBuildNumber());
        Mockito.verify(buildCapabilityProcess.scheduler, Mockito.times(0)).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_still_building_in_software() throws Exception {
        Project project = initProject(42, false, 42, true);

        int[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new int[] {}, buildIdToUpdate);
        Assert.assertTrue(project.getLastBuild().isBuilding());
        Assert.assertEquals(42, project.getLastBuildNumber());
        Mockito.verify(buildCapabilityProcess.scheduler).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    //    ////////////////////////////////
    //
    //    BuildCapabilityProcess projectEnhancerService = new BuildCapabilityProcess();
    //
    //    ConnectedProject projectToEnhance;
    //
    //    @Before
    //    public void init() {
    //        SoftwareProjectId projectId = new SoftwareProjectId("value");
    //        projectToEnhance = new ConnectedProject(projectId, null);
    //    }
    //
    //    @Ignore
    //    @Test
    //    public void should_merge_with_one_build_plugin() throws Exception {
    //        BasicCapability buildPlugin = Mockito.mock(BasicCapability.class);
    //
    //        Build completedBuild = new Build(42);
    //        Build currentBuild = new Build(43);
    //
    //        ConnectedProject projectFromBuildPlugin = new ConnectedProject("name");
    //        projectFromBuildPlugin.setBuildNumbers(new int[] { 1, 2, 3 });
    //        projectFromBuildPlugin.setCompletedBuild(completedBuild);
    //        projectFromBuildPlugin.setCurrentBuild(currentBuild);
    //        projectFromBuildPlugin.setDescription("description");
    //
    //        ProjectId projectId = projectToEnhance.getProjectId();
    //        // when(buildPlugin.findProject(projectId)).thenReturn(projectFromBuildPlugin);
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin);
    //
    //        assertArrayEquals(new int[] { 1, 2, 3 }, projectToEnhance.getBuildNumbers());
    //        assertEquals(completedBuild, projectToEnhance.getCompletedBuild());
    //        assertEquals(currentBuild, projectToEnhance.getCurrentBuild());
    //        assertEquals("description", projectToEnhance.getDescription());
    //        assertEquals("name", projectToEnhance.getName());
    //    }
    //
    //    @Ignore
    //    @Test
    //    public void should_merge_with_two_build_plugins() throws Exception {
    //        BasicCapability buildPlugin1 = Mockito.mock(BasicCapability.class);
    //        BasicCapability buildPlugin2 = Mockito.mock(BasicCapability.class);
    //
    //        ConnectedProject projectFromBuildPlugin1 = new ConnectedProject("name1");
    //        projectFromBuildPlugin1.setDescription("description");
    //
    //        ConnectedProject projectFromBuildPlugin2 = new ConnectedProject("name2");
    //
    //        ProjectId projectId = projectToEnhance.getProjectId();
    //        // when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
    //        // when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);
    //
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin1);
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin2);
    //
    //        assertEquals("description", projectToEnhance.getDescription());
    //        assertEquals("name2", projectToEnhance.getName());
    //    }
    //
    //    @Ignore
    //    @Test
    //    public void last_plugin_is_always_right() throws Exception {
    //        BasicCapability buildPlugin1 = Mockito.mock(BasicCapability.class);
    //        BasicCapability buildPlugin2 = Mockito.mock(BasicCapability.class);
    //
    //        ConnectedProject projectFromBuildPlugin1 = new ConnectedProject("name1");
    //        projectFromBuildPlugin1.setDescription("description1");
    //
    //        ConnectedProject projectFromBuildPlugin2 = new ConnectedProject("name2");
    //        projectFromBuildPlugin2.setDescription("description2");
    //
    //        ProjectId projectId = projectToEnhance.getProjectId();
    //        // when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
    //        // when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);
    //
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin1);
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin2);
    //
    //        assertEquals("description2", projectToEnhance.getDescription());
    //    }
    //
    //    @Ignore
    //    @Test
    //    public void should_not_fail_if_project_is_not_found() throws Exception {
    //        BasicCapability buildPlugin = Mockito.mock(BasicCapability.class);
    //        ProjectId projectId = projectToEnhance.getProjectId();
    //        // when(buildPlugin.findProject(projectId)).thenThrow(new ProjectNotFoundException("project not found"));
    //        //        projectEnhancerService.enhanceWithBuildInformations(projectToEnhance, buildPlugin);
    //    }
}
