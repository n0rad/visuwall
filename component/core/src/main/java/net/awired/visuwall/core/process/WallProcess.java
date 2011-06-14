package net.awired.visuwall.core.process;

import java.util.List;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.core.domain.ConnectedProject;
import net.awired.visuwall.core.persistence.entity.SoftwareAccess;
import net.awired.visuwall.core.persistence.entity.Wall;
import net.awired.visuwall.core.service.BuildProjectService;
import net.awired.visuwall.core.service.PluginService;
import net.awired.visuwall.core.service.ProjectAggregatorService;
import net.awired.visuwall.core.service.SoftwareAccessService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;

@Component
public class WallProcess {

    private static final Logger LOG = LoggerFactory.getLogger(WallProcess.class);

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    BuildProjectService projectService;

    @Autowired
    PluginService pluginService;

    @Autowired
    ProjectAggregatorService projectAggregatorService;

    @Autowired
    SoftwareAccessService softwareAccessService;

    ///////////////////////////////////////////////////////////////

    public void rebuildFullWallInformations(Wall wall) {
        rebuildConnectionPluginsInSoftwareAccess(wall);
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            Runnable task = getDiscoverAndAddProjectsTask(wall, softwareAccess);
            @SuppressWarnings("unchecked")
            ScheduledFuture<Object> futur = taskScheduler.scheduleWithFixedDelay(task,
                    softwareAccess.getProjectFinderDelaySecond() * 1000);
            softwareAccess.setProjectFinderTask(futur);
        }
    }

    private void rebuildConnectionPluginsInSoftwareAccess(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            VisuwallPlugin<Connection> plugin = pluginService.getPluginFromUrl(softwareAccess.getUrl());
            Connection connection = plugin.getConnection(softwareAccess.getUrl().toString(), null);
            softwareAccess.setConnection(connection);
        }
    }

    private Runnable getDiscoverAndAddProjectsTask(final Wall theWall, final SoftwareAccess theSoftwareAccess) {
        return new Runnable() {
            Wall wall = theWall;
            SoftwareAccess softwareAccess = theSoftwareAccess;

            @Override
            public void run() {
                LOG.info("Running Project Discover task for " + softwareAccess + " in wall " + theWall);
                if (softwareAccess.getConnection() instanceof BuildCapability) {
                    BuildCapability buildPlugin = (BuildCapability) softwareAccess.getConnection();
                    Set<ProjectId> projectIds = softwareAccessService.discoverBuildProjects(softwareAccess);
                    for (ProjectId projectId : projectIds) {
                        if (containsId(wall.getProjects(), projectId)) {
                            // this project is already registered in list
                            continue;
                        }

                        createProject(buildPlugin, projectId);
                    }
                } else {
                    for (ConnectedProject project : wall.getProjects()) {
                        boolean contains = softwareAccess.getConnection().contains(project.getProjectId());
                        if (contains) {
                            project.getCapabilities().add(softwareAccess.getConnection());
                        }
                    }
                }

            }

            private void createProject(BuildCapability buildPlugin, ProjectId projectId) {
                ConnectedProject connectedProject = new ConnectedProject(projectId);
                connectedProject.setBuildPlugin(buildPlugin);
                connectedProject.getCapabilities().add(buildPlugin);
                Runnable statusTask = projectService.getStatusTask(connectedProject);
                @SuppressWarnings("unchecked")
                ScheduledFuture<Object> scheduleTask = taskScheduler.scheduleAtFixedRate(statusTask,
                        softwareAccess.getProjectStatusDelaySecond() * 1000);
                connectedProject.setProjectStatusTask(scheduleTask);
                //TODO MOVE
                //                    projectAggregatorService.enhanceWithBuildInformations(connectedProject, buildPlugin);
                wall.getProjects().add(connectedProject);
            }
        };
    }

    // TODO move to project List<> in wall !?
    private boolean containsId(List<ConnectedProject> projects, ProjectId projectId) {
        for (Project project : projects) {
            if (project.getProjectId().equals(projectId)) {
                return true;
            }
        }
        return false;
    }

}
