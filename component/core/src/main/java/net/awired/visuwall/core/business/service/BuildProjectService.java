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
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import com.google.common.base.Preconditions;

@Service
public class BuildProjectService {

    private static final Logger LOG = LoggerFactory.getLogger(BuildProjectService.class);

    @Autowired
    ProjectAggregatorService projectEnhancerService;

    private static final int PROJECT_NOT_BUILT_ID = -1;

    @Transient
    String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

    public void updateProject(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        for (BasicCapability service : project.getCapabilities()) {
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
    public Date getEstimatedFinishTime(ConnectedProject project) throws ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        Date estimatedFinishTime = project.getBuildPlugin().getEstimatedFinishTime(project.getProjectId());
        if (estimatedFinishTime != null) {
            return estimatedFinishTime;
        }
        throw new RuntimeException("estimatedFinishTime null");
    }

    public Runnable getStatusTask(final ConnectedProject theProject) {
        Preconditions.checkNotNull(theProject, "project can not be null");
        return new Runnable() {
            ConnectedProject project = theProject;

            @Override
            public void run() {
                LOG.info("Running Project status task for " + project);
                //                project.setState(getState(project));
                //                project.setBuilding(isBuilding(project));
                project.setCurrentBuildId(getLastBuildNumber(project));
            }
        };
    }

    public Build findBuildByBuildNumber(ConnectedProject project, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            Build build = project.getBuildPlugin().findBuildByBuildNumber(project.getProjectId(), buildNumber);
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

    //////////////////////////////////////////////////////////////////////////////////////

    private int getLastBuildNumber(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            return project.getBuildPlugin().getLastBuildNumber(project.getProjectId());
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
            return project.getBuildPlugin().getState(project.getProjectId());
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
            return project.getBuildPlugin().isBuilding(project.getProjectId());
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        // TODO exception?
        return false;
    }

}
