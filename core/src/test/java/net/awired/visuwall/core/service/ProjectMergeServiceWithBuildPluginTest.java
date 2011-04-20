package net.awired.visuwall.core.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.core.service.ProjectMergeService;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectMergeServiceWithBuildPluginTest {

    ProjectMergeService projectMergeService = new ProjectMergeService();

    Project projectToMerge;

    @Before
    public void init() {
        ProjectId projectId = new ProjectId();
        projectId.addId("id", "value");
        projectToMerge = new Project(projectId);
    }

    @Test
    public void should_merge_with_one_build_plugin() throws ProjectNotFoundException {
        BuildConnectionPlugin buildPlugin = Mockito.mock(BuildConnectionPlugin.class);

        Build completedBuild = new Build();
        Build currentBuild = new Build();

        Project projectFromBuildPlugin = new Project("name");
        projectFromBuildPlugin.setBuildNumbers(new int[]{1,2,3});
        projectFromBuildPlugin.setCompletedBuild(completedBuild);
        projectFromBuildPlugin.setCurrentBuild(currentBuild);
        projectFromBuildPlugin.setDescription("description");
        projectFromBuildPlugin.setState(State.UNSTABLE);

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin.findProject(projectId)).thenReturn(projectFromBuildPlugin);
        projectMergeService.merge(projectToMerge, buildPlugin);

        assertArrayEquals(new int[]{1,2,3}, projectToMerge.getBuildNumbers());
        assertEquals(completedBuild, projectToMerge.getCompletedBuild());
        assertEquals(currentBuild, projectToMerge.getCurrentBuild());
        assertEquals("description", projectToMerge.getDescription());
        assertEquals(State.UNSTABLE, projectToMerge.getState());
        assertEquals("name", projectToMerge.getName());
    }

    @Test
    public void should_merge_with_two_build_plugins() throws ProjectNotFoundException {
        BuildConnectionPlugin buildPlugin1 = Mockito.mock(BuildConnectionPlugin.class);
        BuildConnectionPlugin buildPlugin2 = Mockito.mock(BuildConnectionPlugin.class);

        Project projectFromBuildPlugin1 = new Project("name1");
        projectFromBuildPlugin1.setDescription("description");

        Project projectFromBuildPlugin2 = new Project("name2");

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
        when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

        projectMergeService.merge(projectToMerge, buildPlugin1);
        projectMergeService.merge(projectToMerge, buildPlugin2);

        assertEquals("description", projectToMerge.getDescription());
        assertEquals("name2", projectToMerge.getName());
    }

    @Test
    public void last_plugin_is_always_right() throws ProjectNotFoundException {
        BuildConnectionPlugin buildPlugin1 = Mockito.mock(BuildConnectionPlugin.class);
        BuildConnectionPlugin buildPlugin2 = Mockito.mock(BuildConnectionPlugin.class);

        Project projectFromBuildPlugin1 = new Project("name1");
        projectFromBuildPlugin1.setDescription("description1");

        Project projectFromBuildPlugin2 = new Project("name2");
        projectFromBuildPlugin2.setDescription("description2");

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
        when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

        projectMergeService.merge(projectToMerge, buildPlugin1);
        projectMergeService.merge(projectToMerge, buildPlugin2);

        assertEquals("description2", projectToMerge.getDescription());
    }

    @Test
    public void should_not_fail_if_project_is_not_found() throws ProjectNotFoundException {
        BuildConnectionPlugin buildPlugin = Mockito.mock(BuildConnectionPlugin.class);
        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin.findProject(projectId)).thenThrow(new ProjectNotFoundException("project not found"));
        projectMergeService.merge(projectToMerge, buildPlugin);
    }
}
