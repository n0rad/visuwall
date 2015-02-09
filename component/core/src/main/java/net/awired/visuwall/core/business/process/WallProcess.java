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

package fr.norad.visuwall.core.business.process;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ScheduledFuture;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.exception.ProjectNotFoundException;
import fr.norad.visuwall.api.plugin.capability.BasicCapability;
import fr.norad.visuwall.api.plugin.capability.BuildCapability;
import fr.norad.visuwall.core.business.domain.Project;
import fr.norad.visuwall.core.business.service.PluginServiceInterface;
import fr.norad.visuwall.core.business.service.ProjectService;
import fr.norad.visuwall.core.business.service.SoftwareAccessService;
import fr.norad.visuwall.core.persistence.entity.SoftwareAccess;
import fr.norad.visuwall.core.persistence.entity.Wall;
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
    PluginServiceInterface pluginService;

    @Autowired
    SoftwareAccessService softwareAccessService;

    // /////////////////////////////////////////////////////////////

    public void rebuildFullWallInformations(final Wall wall) {
        taskScheduler.schedule(new Runnable() {
            @Override
            public void run() {
                // TODO prevent wall hiding (exception causing wall not added to wall list) if software not found
                rebuildConnectionPluginsInSoftwareAccess(wall);
                for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
                    if (softwareAccess.getConnection() instanceof BuildCapability) {
                        Runnable task = getDiscoverBuildProjectsRunner(wall, softwareAccess);
                        task.run();
                        // TODO skip first immediate schedule run as called by hand upper
                        @SuppressWarnings("unchecked")
                        ScheduledFuture<Object> futur = taskScheduler.scheduleWithFixedDelay(task,
                                softwareAccess.getProjectFinderDelaySecond() * 1000);
                        softwareAccess.setProjectFinderTask(futur);
                    }
                }

                // here as task war run ones without schedule, projects exists, we need that for first run
                // to add other software without waiting for the second project discover
                for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
                    if (!(softwareAccess.getConnection() instanceof BuildCapability)) {
                        Runnable task = getDiscoverOtherProjectsRunner(wall, softwareAccess);
                        task.run();
                        // TODO skip first immediate schedule run as called by hand upper
                        @SuppressWarnings("unchecked")
                        ScheduledFuture<Object> futur = taskScheduler.scheduleWithFixedDelay(task,
                                softwareAccess.getProjectFinderDelaySecond() * 1000);
                        softwareAccess.setProjectFinderTask(futur);
                    }
                }

            }
        }, new Date());
    }

    private void rebuildConnectionPluginsInSoftwareAccess(Wall wall) {
        for (SoftwareAccess softwareAccess : wall.getSoftwareAccesses()) {
            try {
                Map<String, String> properties = new HashMap<String, String>(softwareAccess.getProperties());
                //TODO 
                if (softwareAccess.getLogin() != null) {
                    properties.put("login", softwareAccess.getLogin());
                }
                if (softwareAccess.getPassword() != null) {
                    properties.put("password", softwareAccess.getPassword());
                }
                BasicCapability connection = pluginService.getPluginConnectionFromUrl(softwareAccess.getUrl(),
                        properties);
                softwareAccess.setConnection(connection);
            } catch (Throwable e) {
                LOG.warn("Plugin throw an exception", e);
            }
        }
    }

    private Runnable getDiscoverBuildProjectsRunner(final Wall wall, final SoftwareAccess softwareAccess) {
        if (!(softwareAccess.getConnection() instanceof BuildCapability)) {
            throw new RuntimeException("Software should be a build one " + softwareAccess);
        }
        return new Runnable() {
            @Override
            public void run() {
                LOG.debug("Running Project Discover task for " + softwareAccess + " in wall " + wall);
                Set<SoftwareProjectId> projectIds = softwareAccessService.discoverBuildProjects(softwareAccess);
                List<SoftwareProjectId> wallBuildProjectIds = wall.getProjects().getBuildProjectIds();
                for (SoftwareProjectId projectId : projectIds) {
                    if (wallBuildProjectIds.contains(projectId)) {
                        continue;
                    }
                    Runnable projectCreationRunner = WallProcess.this.projectService.getProjectCreationRunner(wall,
                            softwareAccess, projectId);
                    projectCreationRunner.run();
                    // taskScheduler.schedule(projectCreationRunner, new Date());
                }
            }
        };
    }

    private Runnable getDiscoverOtherProjectsRunner(final Wall wall, final SoftwareAccess softwareAccess) {
        if (softwareAccess.getConnection() instanceof BuildCapability) {
            throw new RuntimeException("Software should not be a build one " + softwareAccess);
        }
        return new Runnable() {
            @Override
            public void run() {
                for (Project project : wall.getProjects()) {
                    try {
                        SoftwareProjectId softwareProjectId = softwareAccess.getConnection().identify(
                                project.getProjectKey());
                        project.getCapabilities().put(softwareProjectId, softwareAccess.getConnection());
                    } catch (ProjectNotFoundException e) {
                        LOG.debug("ProjectKey {} not found in software {}", project.getProjectKey(), softwareAccess);
                    }
                }
            }
        };
    }
}
