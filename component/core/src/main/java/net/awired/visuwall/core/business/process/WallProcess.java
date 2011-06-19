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
import net.awired.visuwall.core.business.service.PluginService;
import net.awired.visuwall.core.business.service.ProjectService;
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
    ProjectService projectService;

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
                LOG.warn("Can't rebuild connection. " + softwareAccess, e);
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
                        if (wall.getProjects().containsId(projectId)) {
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
