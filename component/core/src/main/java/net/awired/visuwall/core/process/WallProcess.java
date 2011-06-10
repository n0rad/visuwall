package net.awired.visuwall.core.process;

import java.util.List;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.StatePlugin;
import net.awired.visuwall.core.domain.ConnectedProject;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.core.domain.Wall;
import net.awired.visuwall.core.service.PluginService;
import net.awired.visuwall.core.service.ProjectAggregatorService;
import net.awired.visuwall.core.service.ProjectService;
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
    ProjectService projectService;

    @Autowired
    @VisibleForTesting
    PluginService pluginService;

    @Autowired
    ProjectAggregatorService projectAggregatorService;

    public void reconstructWallTransientInfo(Wall wall) {
        Preconditions.checkNotNull(wall, "Wall is a mandatory parameter");

        this.rebuildConnectionPluginsInSoftwareAccess(wall);
        this.rebuildConnectionPluginsAssociatedToProjects(wall);

        //updateWallProjects(connectionPlugins, wall);
        if (LOG.isInfoEnabled()) {
            LOG.info("Done refreshing wall : " + wall + " and its " + wall.getProjects().size() + " projects");
        }
    }

    private void rebuildConnectionPluginsAssociatedToProjects(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            ConnectionPlugin connectionPlugin = softwareAccess.getConnectionPlugin();
            if (!(connectionPlugin instanceof StatePlugin)) {
                continue;
            }

            if (softwareAccess.isAllProject()) {
                List<ProjectId> projectIds;
                try {
                    projectIds = connectionPlugin.findAllProjects();
                    insertProjectByIds(wall, projectIds, connectionPlugin);
                } catch (NotImplementedOperationException e) {
                    LOG.debug("plugin " + connectionPlugin + " does not support findAllProjects");
                }
            } else {
                List<ProjectId> nameProjectIds = connectionPlugin.findProjectsByNames(softwareAccess
                        .getProjectNames());
                insertProjectByIds(wall, nameProjectIds, connectionPlugin);

                List<ProjectId> viewProjectIds = connectionPlugin.findProjectsByViews(softwareAccess.getViewNames());
                insertProjectByIds(wall, viewProjectIds, connectionPlugin);
            }

            // loop with contains
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

    }

    private void insertProjectByIds(Wall wall, List<ProjectId> projectIds, ConnectionPlugin connectionPlugin) {
        for (ProjectId projectId : projectIds) {
            ConnectedProject connectedProject = new ConnectedProject(projectId);
            connectedProject.getConnectionPlugins().add(connectionPlugin);
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
