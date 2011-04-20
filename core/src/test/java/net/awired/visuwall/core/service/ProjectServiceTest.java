package net.awired.visuwall.core.service;

import static org.junit.Assert.fail;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.core.domain.PluginHolder;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.exception.NotCreatedException;
import net.awired.visuwall.plugin.sonar.SonarConnectionPlugin;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.mockito.Mockito;

public class ProjectServiceTest {

	ProjectService projectService;
	
	@Before
	public void init() {
		projectService = new ProjectService();
		ProjectMergeService projectMergeService = Mockito.mock(ProjectMergeService.class);
		projectService.projectMergeService = projectMergeService;
	}

    @Test (expected = NullPointerException.class)
    public void should_not_accept_null_parameter() throws NotCreatedException {
    	projectService.updateProject(null, new Project());
    	projectService.updateProject(new PluginHolder(), null);
    }
    
    public PluginHolder getPluginHolder() {
    	PluginHolder pluginHolder = new PluginHolder();
    	BuildConnectionPlugin buildConnectionPlugin = Mockito.mock(BuildConnectionPlugin.class);
    	QualityConnectionPlugin qualityConnectionPlugin = Mockito.mock(QualityConnectionPlugin.class);
    	pluginHolder.addBuildService(buildConnectionPlugin);
    	pluginHolder.addQualityService(qualityConnectionPlugin);
    	
    	List<ProjectId> projectIds = new ArrayList<ProjectId>();
    	projectIds.add(new ProjectId());
    	projectIds.add(new ProjectId());
        when(buildConnectionPlugin.findAllProjects()).thenReturn(projectIds);
    	return pluginHolder;
    }
    
    @Ignore
    @Test
    public void test() {
    	Wall wall = new Wall();
    	wall.setPluginHolder(getPluginHolder());
    	projectService.updateWallProjects(wall);
    }
    
    @Test
    public void should_call_merge_for_plugins() {
    	PluginHolder pluginHolder = getPluginHolder();
    	Project project = new Project();
    	project.setProjectId(new ProjectId());
    	projectService.updateProject(pluginHolder, project);

    	
    	Mockito.verify(projectService.projectMergeService).merge(project, pluginHolder.getBuildServices().iterator().next());
    	Mockito.verify(projectService.projectMergeService).merge(project, pluginHolder.getQualityServices().iterator().next(), projectService.metrics);
    }
}
