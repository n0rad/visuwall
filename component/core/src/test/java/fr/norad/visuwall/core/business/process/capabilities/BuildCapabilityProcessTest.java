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
package fr.norad.visuwall.core.business.process.capabilities;

import java.util.Date;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.plugin.capability.BuildCapability;
import fr.norad.visuwall.core.business.domain.Build;
import fr.norad.visuwall.core.business.domain.Project;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;
import org.springframework.scheduling.TaskScheduler;

public class BuildCapabilityProcessTest {

    private BuildCapabilityProcess buildCapabilityProcess = new BuildCapabilityProcess();

    private static Project initProject(String previousBuildId, boolean previousBuilding, String newBuildId, boolean building)
            throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("name");
        BuildCapability buildCapability = Mockito.mock(BuildCapability.class);
        Mockito.when(buildCapability.getLastBuildId(projectId)).thenReturn(newBuildId);
        Mockito.when(buildCapability.isBuilding(projectId, newBuildId)).thenReturn(building);
        Project project = new Project(projectId, buildCapability);
        project.setLastBuildId(previousBuildId);
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
        Project project = initProject("42", false, "43", false);

        String[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new String[] { "43" }, buildIdToUpdate);
        Assert.assertFalse(project.getLastBuild().isBuilding());
        Assert.assertEquals("43", project.getLastBuildId());
        Mockito.verify(buildCapabilityProcess.scheduler, Mockito.times(0)).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_new_build_building_in_software() throws Exception {
        Project project = initProject("42", false, "43", true);

        String[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new String[] {}, buildIdToUpdate);
        Assert.assertTrue(project.getLastBuild().isBuilding());
        Assert.assertEquals("43", project.getLastBuildId());
        Mockito.verify(buildCapabilityProcess.scheduler).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_no_new_build_in_software() throws Exception {
        Project project = initProject("42", false, "42", false);

        String[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new String[] {}, buildIdToUpdate);
        Assert.assertFalse(project.getLastBuild().isBuilding());
        Assert.assertEquals("42", project.getLastBuildId());
        Mockito.verify(buildCapabilityProcess.scheduler, Mockito.times(0)).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

    @Test
    public void should_not_return_update_needed_when_still_building_in_software() throws Exception {
        Project project = initProject("42", false, "42", true);

        String[] buildIdToUpdate = buildCapabilityProcess.updateStatusAndReturnBuildsToUpdate(project);

        Assert.assertArrayEquals(new String[] {}, buildIdToUpdate);
        Assert.assertTrue(project.getLastBuild().isBuilding());
        Assert.assertEquals("42", project.getLastBuildId());
        Mockito.verify(buildCapabilityProcess.scheduler).schedule(Matchers.any(Runnable.class),
                Matchers.any(Date.class));
    }

}
