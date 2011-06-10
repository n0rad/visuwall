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

package net.awired.visuwall.core.service;

import java.util.Date;
import java.util.List;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.core.domain.ConnectedProject;
import net.awired.visuwall.core.domain.Wall;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class ProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);

    @Autowired
    ProjectAggregatorService projectEnhancerService;

    private static final int PROJECT_NOT_BUILT_ID = -1;

    @Transient
    String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

    public void updateProject(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        for (ConnectionPlugin service : project.getConnectionPlugins()) {
            projectEnhancerService.enhanceWithBuildInformations(project, service);
            projectEnhancerService.enhanceWithQualityAnalysis(project, service, metrics);
        }
        if (LOG.isDebugEnabled()) {
            LOG.debug(project.toString());
        }
    }

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    public Date getEstimatedFinishTime(Wall wall, String projectName) throws ProjectNotFoundException {
        Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
        Preconditions.checkNotNull(projectName, "projectName is a mandatory parameter");

        ConnectedProject project = wall.getProjectByName(projectName);
        for (ConnectionPlugin service : project.getConnectionPlugins()) {
            try {
                Date estimatedFinishTime = service.getEstimatedFinishTime(project.getProjectId());
                if (estimatedFinishTime != null) {
                    return estimatedFinishTime;
                }
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (NotImplementedOperationException e) {
                // TODO NotImplementedOperationException
            }
        }
        return null;
    }

    public int getLastBuildNumber(List<ConnectionPlugin> connectionPlugins, ProjectId projectId) {
        Preconditions.checkNotNull(connectionPlugins, "connectionPlugins is a mandatory parameter");
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

        for (ConnectionPlugin service : connectionPlugins) {
            try {
                return service.getLastBuildNumber(projectId);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (BuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (NotImplementedOperationException e) {
                // TODO NotImplementedOperationException
            }
        }
        return PROJECT_NOT_BUILT_ID;
    }

    public State getState(List<ConnectionPlugin> connectionPlugins, ProjectId projectId) {
        Preconditions.checkNotNull(connectionPlugins, "connectionPlugins is a mandatory parameter");
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

        for (ConnectionPlugin service : connectionPlugins) {
            try {
                return service.getState(projectId);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (NotImplementedOperationException e) {
                // TODO NotImplementedOperationException
            }
        }
        throw new RuntimeException("Project " + projectId + " must have a state. It can't be found in "
                + connectionPlugins);
    }

    public boolean isBuilding(List<ConnectionPlugin> connectionPlugins, ProjectId projectId) {
        Preconditions.checkNotNull(connectionPlugins, "connectionPlugins is a mandatory parameter");
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");

        for (ConnectionPlugin service : connectionPlugins) {
            try {
                return service.isBuilding(projectId);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (NotImplementedOperationException e) {
                // TODO NotImplementedOperationException
            }
        }
        return false;
    }

    public Build findBuildByBuildNumber(Wall wall, String projectName, int buildNumber)
            throws BuildNotFoundException, ProjectNotFoundException {
        Preconditions.checkNotNull(wall, "wall is a mandatory parameter");
        Preconditions.checkNotNull(projectName, "projectName is a mandatory parameter");

        ConnectedProject project = wall.getProjectByName(projectName);
        for (ConnectionPlugin service : project.getConnectionPlugins()) {
            try {
                Build build = service.findBuildByBuildNumber(project.getProjectId(), buildNumber);
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
            } catch (NotImplementedOperationException e) {
                // TODO NotImplementedOperationException
            }
        }
        throw new BuildNotFoundException("No build #" + buildNumber + " for project " + project.getProjectId());
    }

}
