package net.awired.visuwall.core.process;

import java.util.List;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BuildPlugin;
import net.awired.visuwall.api.plugin.capability.ViewPlugin;
import net.awired.visuwall.core.domain.ConnectedProject;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.service.BuildProjectService;
import net.awired.visuwall.core.service.PluginService;
import net.awired.visuwall.core.service.ProjectAggregatorService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

@Component
public class WallProcess {

    private static final Logger LOG = LoggerFactory.getLogger(WallProcess.class);

    @Autowired
    @VisibleForTesting
    BuildProjectService projectService;

    @Autowired
    @VisibleForTesting
    PluginService pluginService;

    @Autowired
    ProjectAggregatorService projectAggregatorService;

    public void reconstructWallTransientInfo(Wall wall) {
        Preconditions.checkNotNull(wall, "Wall is a mandatory parameter");

        this.rebuildConnectionPluginsInSoftwareAccess(wall);
        this.rebuildProjectsWithAssociatedBuildPlugin(wall);
        this.associateOtherPluginsWithProjects(wall);

        //updateWallProjects(connectionPlugins, wall);
        if (LOG.isInfoEnabled()) {
            LOG.info("Done refreshing wall : " + wall + " and its " + wall.getProjects().size() + " projects");
        }
    }

    ///////////////////////////////////////////////////////////////S

    private void associateOtherPluginsWithProjects(Wall wall) {
        for (ConnectedProject project : wall.getProjects()) {
            for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
                ConnectionPlugin connectionPlugin = softwareAccess.getConnectionPlugin();
                if (project.getConnectionPlugins().contains(connectionPlugin)) {
                    // skip already associated plugin wich is the build one
                    continue;
                }

                if (connectionPlugin.contains(project.getProjectId())) {
                    project.getConnectionPlugins().add(connectionPlugin);
                }
            }
        }
    }

    private void rebuildProjectsWithAssociatedBuildPlugin(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            if (!(softwareAccess.getConnectionPlugin() instanceof BuildPlugin)) {
                continue;
            }
            BuildPlugin buildPlugin = (BuildPlugin) softwareAccess.getConnectionPlugin();

            if (softwareAccess.isAllProject()) {
                List<ProjectId> projectIds;
                projectIds = buildPlugin.findAllProjects();
                insertProjectByIds(wall, projectIds, buildPlugin);
            } else {
                List<ProjectId> nameProjectIds = buildPlugin.findProjectsByNames(softwareAccess.getProjectNames());

                if (nameProjectIds == null) {
                    LOG.warn("plugin return null on findProjectsByNames", buildPlugin);
                } else {
                    insertProjectByIds(wall, nameProjectIds, buildPlugin);
                }

                if (buildPlugin instanceof ViewPlugin) {
                    List<ProjectId> viewProjectIds = ((ViewPlugin) buildPlugin).findProjectsByViews(softwareAccess
                            .getViewNames());
                    if (nameProjectIds == null) {
                        LOG.warn("plugin return null on findProjectsByViews", buildPlugin);
                    } else {
                        insertProjectByIds(wall, viewProjectIds, buildPlugin);
                    }
                }
            }
        }
    }

    //        projectAggregatorService.enhanceWithBuildInformations(connectedProject,
    //                softwareAccess.getConnectionPlugin());

    //        for (ConnectionPlugin connectionPlugin : connectionPlugins) {
    //            try {connect
    //                List<ProjectId> discoveredProjects = connectionPlugin.findAllProjects();
    //                for (ProjectId discoveredProjectId : discoveredProjects) {
    //                    ConnectedProject project;
    //                    try {
    //                        project = wall.getProjectByProjectId(discoveredProjectId);
    //                    } catch (ProjectNotFoundException e) {
    //                        project = new ConnectedProject(discoveredProjectId);
    //                        project.setConnectionPlugins(connectionPlugins);
    //                        wall.getProjects().add(project);
    //                    }
    //                    updateProject(project);
    //                }
    //            } catch (NotImplementedOperationException e) {
    //            }
    //        }

    private void insertProjectByIds(Wall wall, List<ProjectId> projectIds, BuildPlugin buildPlugin) {
        for (ProjectId projectId : projectIds) {
            ConnectedProject connectedProject = new ConnectedProject(projectId);
            connectedProject.setBuildPlugin(buildPlugin);
            connectedProject.getConnectionPlugins().add(buildPlugin);
            wall.getProjects().add(connectedProject);
        }
    }

    private void rebuildConnectionPluginsInSoftwareAccess(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            VisuwallPlugin plugin = pluginService.getPluginFromUrl(softwareAccess.getUrl());
            ConnectionPlugin connectionPlugin = plugin.getConnection(softwareAccess.getUrl().toString(), null);
            softwareAccess.setConnectionPlugin(connectionPlugin);
        }
    }

}
