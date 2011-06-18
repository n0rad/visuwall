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

import java.util.Date;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import net.awired.visuwall.core.persistence.entity.SoftwareAccess;
import net.awired.visuwall.core.persistence.entity.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class BuildProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildProjectService.class);

    @Autowired
    ProjectAggregatorService projectEnhancerService;

    @Autowired
    TaskScheduler taskScheduler;

    private static final int PROJECT_NOT_BUILT_ID = -1;

    @Transient
    String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

    public Runnable getProjectCreationRunner(final Wall wallWhereToAdd, final SoftwareAccess buildSoftwareAccess,
            final ProjectId projectId) {
        Preconditions.checkState(buildSoftwareAccess.getConnection() instanceof BuildCapability,
                "softwareAccess needs to point to BuildCapability plugin connection");
        return new Runnable() {
            @SuppressWarnings("unchecked")
            @Override
            public void run() {
                LOG.info("Running project creation task for project id " + projectId + " on software "
                        + buildSoftwareAccess + " in wall " + wallWhereToAdd);
                BuildCapability buildConnection = (BuildCapability) buildSoftwareAccess.getConnection();

                ConnectedProject connectedProject = new ConnectedProject(projectId);
                connectedProject.setBuildConnection(buildConnection);
                connectedProject.getCapabilities().add(buildConnection);
                Runnable updateProjectRunner = getUpdateProjectRunner(connectedProject);

                LOG.debug("Launching first project fill for project" + connectedProject);
                updateProjectRunner.run();

                ScheduledFuture<Object> updateProjectTask = taskScheduler.scheduleAtFixedRate(updateProjectRunner,
                        buildSoftwareAccess.getProjectStatusDelaySecond() * 1000);
                connectedProject.setUpdateProjectTask(updateProjectTask);
                LOG.debug("Adding project " + connectedProject + " to wall " + wallWhereToAdd);
                wallWhereToAdd.getProjects().add(connectedProject);
            }
        };
    }

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    private Date getEstimatedFinishTime(ConnectedProject project) throws ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        Date estimatedFinishTime = project.getBuildConnection().getEstimatedFinishTime(project.getProjectId());
        if (estimatedFinishTime != null) {
            return estimatedFinishTime;
        }
        throw new RuntimeException("estimatedFinishTime null");
    }

    Runnable getUpdateProjectRunner(final ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {
            @Override
            public void run() {
                LOG.info("Running Project Updater task for project " + project);

                //                Preconditions.checkNotNull(project, "project is a mandatory parameter");
                //                for (BasicCapability service : project.getCapabilities()) {
                //                    projectEnhancerService.enhanceWithBuildInformations(project, service);
                //                    projectEnhancerService.enhanceWithQualityAnalysis(project, service, metrics);
                //                }
                //                if (LOG.isDebugEnabled()) {
                //                    LOG.debug(project.toString());
                //                }

                //TODO MOVE
                //                    projectAggregatorService.enhanceWithBuildInformations(connectedProject, buildPlugin);

                project.setState(getState(project));
                project.setBuilding(isBuilding(project));
                project.setCurrentBuildId(getLastBuildNumber(project));
            }
        };
    }

    //////////////////////////////////////////////////////////////////////////////////////

    private Build findBuildByBuildNumber(ConnectedProject project, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            Build build = project.getBuildConnection().findBuildByBuildNumber(project.getProjectId(), buildNumber);
            if (build != null) {
                return build;
            }
        } catch (BuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        throw new BuildNotFoundException("No build #" + buildNumber + " for project " + project);
    }

    private int getLastBuildNumber(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            return project.getBuildConnection().getLastBuildNumber(project.getProjectId());
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        } catch (BuildNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        return PROJECT_NOT_BUILT_ID;
    }

    private State getState(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            return project.getBuildConnection().getState(project.getProjectId());
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        throw new RuntimeException("Project " + project + " must have a state. It can't be found in ");
    }

    private boolean isBuilding(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            return project.getBuildConnection().isBuilding(project.getProjectId());
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        // TODO exception?
        return false;
    }

}
