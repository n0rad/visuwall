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

package net.awired.visuwall.core.business.service;

import java.util.List;
import java.util.concurrent.ScheduledFuture;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.core.business.domain.Project;
import net.awired.visuwall.core.business.process.capabilities.BuildCapabilityProcess;
import net.awired.visuwall.core.persistence.entity.SoftwareAccess;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    TaskScheduler taskScheduler;

    @Autowired
    BuildCapabilityProcess buildProcess;

    public Runnable getProjectCreationRunner(final Wall wallWhereToAdd, final SoftwareAccess buildSoftwareAccess,
            final SoftwareProjectId projectId) {
        Preconditions.checkState(buildSoftwareAccess.getConnection() instanceof BuildCapability,
                "softwareAccess needs to point to BuildCapability plugin connection");
        return new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                LOG.info("Running project creation task for project id " + projectId + " on software "
                        + buildSoftwareAccess + " in wall " + wallWhereToAdd);
                BuildCapability buildConnection = (BuildCapability) buildSoftwareAccess.getConnection();

                Project connectedProject = new Project(projectId, buildConnection);
                Runnable updateProjectRunner = getUpdateProjectRunner(wallWhereToAdd, connectedProject);

                LOG.debug("Launching first project updater for " + connectedProject);
                updateProjectRunner.run();

                ScheduledFuture<Object> updateProjectTask = taskScheduler.scheduleAtFixedRate(updateProjectRunner,
                        buildSoftwareAccess.getProjectStatusDelaySecond() * 1000);
                connectedProject.setUpdateProjectTask(updateProjectTask);
                LOG.debug("Adding project " + connectedProject + " to wall " + wallWhereToAdd);
                wallWhereToAdd.getProjects().add(connectedProject);
            }
        };
    }

    Runnable getUpdateProjectRunner(final Wall wall, final Project project) {
        Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {

            private boolean neverRun = true;

            @Override
            public void run() {
                LOG.info("Running Project Updater task for project " + project);
                try {
                    int[] buildsToUpdate = buildProcess.updateStatusAndReturnBuildsToUpdate(project);
                    if (neverRun || buildsToUpdate.length != 0) {
                        if (!neverRun) {
                            LOG.debug("Project build change and needs a update from software " + project);
                        }

                        SoftwareProjectId projectId = project.getBuildProjectId();

                        // name
                        try {
                            String name = project.getBuildConnection().getName(projectId);
                            project.setName(name);
                        } catch (Exception e) {
                            LOG.warn("Can not found project name for project " + project, e);
                        }

                        // description
                        try {
                            String description = project.getBuildConnection().getDescription(projectId);
                            project.setDescription(description);
                        } catch (Exception e) {
                            LOG.warn("Can not found description for project " + project, e);
                        }

                        for (int buildId : buildsToUpdate) {
                            buildProcess.updateBuild(project, buildId);
                        }

                        try {
                            List<Integer> buildNumbers = project.getBuildConnection().getBuildNumbers(projectId);
                            project.setBuildNumbers(buildNumbers);
                            buildProcess.updatePreviousCompletedBuild(project);
                            buildProcess.updateLastNotBuildingNumber(project);
                        } catch (Exception e) {
                            LOG.warn("Can not update previous completed build for project " + project, e);
                        }

                        // TODO check for new software to update
                        // TODO be sure to not remove a project cause of a capability ProjectNotFoundException 

                        // projectEnhancerService.enhanceWithBuildInformations(project, service);
                        // projectEnhancerService.enhanceWithQualityAnalysis(project, service, metrics);
                        // projectAggregatorService.enhanceWithBuildInformations(connectedProject, buildPlugin);                        
                    }

                } catch (ProjectNotFoundException e) {
                    LOG.info("Project not found by build Software, and will be removed");
                    LOG.debug("Project not found cause", e);
                    wall.getProjects().deleteAndCleanProject(project.getId());
                } catch (BuildNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } finally {
                    neverRun = false;
                }
            }
        };
    }
}
