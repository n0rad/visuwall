package net.awired.visuwall.core.business.process;

import java.util.Date;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import net.awired.visuwall.core.business.service.BuildProjectService;
import net.awired.visuwall.core.business.service.PluginService;
import net.awired.visuwall.core.business.service.SoftwareAccessService;
import net.awired.visuwall.core.persistence.entity.SoftwareAccess;
import net.awired.visuwall.core.persistence.entity.Wall;

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
    SoftwareAccessService softwareAccessService;

    ///////////////////////////////////////////////////////////////

    public void rebuildFullWallInformations(Wall wall) {
        //TODO run in thread and prevent wall hiding if software not found
        rebuildConnectionPluginsInSoftwareAccess(wall);
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            Runnable task = getDiscoverProjectsRunner(wall, softwareAccess);
            @SuppressWarnings("unchecked")
            ScheduledFuture<Object> futur = taskScheduler.scheduleWithFixedDelay(task,
                    softwareAccess.getProjectFinderDelaySecond() * 1000);
            softwareAccess.setProjectFinderTask(futur);
        }
    }

    private void rebuildConnectionPluginsInSoftwareAccess(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            try {
                VisuwallPlugin<Connection> plugin = pluginService.getPluginFromUrl(softwareAccess.getUrl());
                Connection connection = plugin.getConnection(softwareAccess.getUrl().toString(), null);
                softwareAccess.setConnection(connection);
            } catch (ConnectionException e) {
                LOG.warn("Can't rebuid connection. " + softwareAccess, e);
            }
        }
    }

    private Runnable getDiscoverProjectsRunner(final Wall wall, final SoftwareAccess softwareAccess) {
        return new Runnable() {
            @Override
            public void run() {
                LOG.info("Running Project Discover task for " + softwareAccess + " in wall " + wall);
                if (softwareAccess.getConnection() instanceof BuildCapability) {
                    Set<ProjectId> projectIds = softwareAccessService.discoverBuildProjects(softwareAccess);
                    for (ProjectId projectId : projectIds) {
                        if (wall.projectsContainsId(projectId)) {
                            // this project is already registered in list
                            continue;
                        }
                        Runnable projectCreationRunner = WallProcess.this.projectService.getProjectCreationRunner(
                                wall, softwareAccess, projectId);
                        taskScheduler.schedule(projectCreationRunner, new Date());
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

        };
    }
}
