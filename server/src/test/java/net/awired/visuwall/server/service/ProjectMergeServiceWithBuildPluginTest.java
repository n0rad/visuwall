package net.awired.visuwall.server.service;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildPlugin;

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
        projectToMerge = new Project();
        projectToMerge.setProjectId(projectId);
    }

    @Test
    public void should_merge_with_one_build_plugin() throws ProjectNotFoundException {
        BuildPlugin buildPlugin = Mockito.mock(BuildPlugin.class);

        Build completedBuild = new Build();
        Build currentBuild = new Build();

        Project projectFromBuildPlugin = new Project();
        projectFromBuildPlugin.setArtifactId("artifactId");
        projectFromBuildPlugin.setBuildNumbers(new int[]{1,2,3});
        projectFromBuildPlugin.setCompletedBuild(completedBuild);
        projectFromBuildPlugin.setCurrentBuild(currentBuild);
        projectFromBuildPlugin.setDescription("description");
        projectFromBuildPlugin.setState(State.UNSTABLE);
        projectFromBuildPlugin.setName("name");

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin.findProject(projectId)).thenReturn(projectFromBuildPlugin);
        projectMergeService.merge(projectToMerge, buildPlugin);

        assertEquals("artifactId", projectToMerge.getArtifactId());
        assertArrayEquals(new int[]{1,2,3}, projectToMerge.getBuildNumbers());
        assertEquals(completedBuild, projectToMerge.getCompletedBuild());
        assertEquals(currentBuild, projectToMerge.getCurrentBuild());
        assertEquals("description", projectToMerge.getDescription());
        assertEquals(State.UNSTABLE, projectToMerge.getState());
        assertEquals("name", projectToMerge.getName());
    }

    @Test
    public void should_merge_with_two_build_plugins() throws ProjectNotFoundException {
        BuildPlugin buildPlugin1 = Mockito.mock(BuildPlugin.class);
        BuildPlugin buildPlugin2 = Mockito.mock(BuildPlugin.class);

        Project projectFromBuildPlugin1 = new Project();
        projectFromBuildPlugin1.setArtifactId("artifactId");

        Project projectFromBuildPlugin2 = new Project();
        projectFromBuildPlugin2.setName("name");

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
        when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

        projectMergeService.merge(projectToMerge, buildPlugin1);
        projectMergeService.merge(projectToMerge, buildPlugin2);

        assertEquals("artifactId", projectToMerge.getArtifactId());
        assertEquals("name", projectToMerge.getName());
    }

    @Test
    public void last_plugin_is_always_right() throws ProjectNotFoundException {
        BuildPlugin buildPlugin1 = Mockito.mock(BuildPlugin.class);
        BuildPlugin buildPlugin2 = Mockito.mock(BuildPlugin.class);

        Project projectFromBuildPlugin1 = new Project();
        projectFromBuildPlugin1.setArtifactId("artifactId1");

        Project projectFromBuildPlugin2 = new Project();
        projectFromBuildPlugin2.setArtifactId("artifactId2");

        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin1.findProject(projectId)).thenReturn(projectFromBuildPlugin1);
        when(buildPlugin2.findProject(projectId)).thenReturn(projectFromBuildPlugin2);

        projectMergeService.merge(projectToMerge, buildPlugin1);
        projectMergeService.merge(projectToMerge, buildPlugin2);

        assertEquals("artifactId2", projectToMerge.getArtifactId());
    }

    @Test
    public void should_not_fail_if_project_is_not_found() throws ProjectNotFoundException {
        BuildPlugin buildPlugin = Mockito.mock(BuildPlugin.class);
        ProjectId projectId = projectToMerge.getProjectId();
        when(buildPlugin.findProject(projectId)).thenThrow(new ProjectNotFoundException("project not found"));
        projectMergeService.merge(projectToMerge, buildPlugin);
    }
}
