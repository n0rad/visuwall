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

package net.awired.visuwall.core.business.process.capabilities;

import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.core.business.domain.Build;
import net.awired.visuwall.core.business.domain.Project;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;

@Component
public class BuildCapabilityProcess {

    private static final Logger LOG = LoggerFactory.getLogger(BuildCapabilityProcess.class);

    @Autowired
    TaskScheduler scheduler;

    public void updatePreviousCompletedBuild(Project project) throws ProjectNotFoundException {
        List<String> buildIds = project.getBuildId();
        if (buildIds.size() < 2) {
            // with only a build there is no previous completed build
            return;
        }
        try {
            int skip = 1;
            if (project.getLastBuild().isBuilding()) {
                if (buildIds.size() < 3) {
                    // first build is building so we do not have enough builds to have a previous completed 
                    return;
                }
                skip = 2;
            }

            ListIterator<String> reverseBuildIt = project.getBuildId().listIterator(
                    project.getBuildId().size());

            for (int i = 0; reverseBuildIt.hasPrevious(); i++) {
                String buildId = reverseBuildIt.previous();
                if (i < skip) {
                    continue;
                }
                Build build = getCreatedWithContentBuild(project, buildId);
                if (build == null) {
                    continue;
                }
                State state = build.getState();
                if (state == State.UNKNOWN || state == State.ABORTED) {
                    continue;
                }

                project.setPreviousCompletedBuildId(build.getBuildId());
                break;
            }
        } catch (BuildNotFoundException e) {
            LOG.warn("last build not found to update previous completed build", e);
        }
    }

    private Build getCreatedWithContentBuild(Project project, String buildId) throws ProjectNotFoundException {
        Build build = project.getBuilds().get(buildId);
        if (build == null) {
            updateBuild(project, buildId);
            build = project.getBuilds().get(buildId);
            if (build == null) {
                LOG.warn("Build " + buildId + " not found after update for project " + project);
            }
        }
        return build;
    }

    public void updateLastNotBuildingId(Project project) throws ProjectNotFoundException {
        ListIterator<String> reverseBuildIt = project.getBuildId().listIterator(
                project.getBuildId().size());
        while (reverseBuildIt.hasPrevious()) {
            String buildId = reverseBuildIt.previous();
            Build build = getCreatedWithContentBuild(project, buildId);
            if (build.isBuilding()) {
                continue;
            }
            project.setLastNotBuildingId(buildId);
            break;
        }
    }

    public void updateBuild(Project project, String buildId) throws ProjectNotFoundException {
        try {
            LOG.info("Updating build " + buildId + " for project " + project);
            SoftwareProjectId projectId = project.getBuildProjectId();
            Build build = project.findCreatedBuild(buildId);

            State state = project.getBuildConnection().getBuildState(projectId, buildId);
            build.setState(state);

            // buildTime
            BuildTime buildTime = project.getBuildConnection().getBuildTime(projectId, buildId);
            build.setStartTime(buildTime.getStartTime());
            build.setDuration(buildTime.getDuration());
            
            List<Commiter> commiters = project.getBuildConnection().getBuildCommiters(projectId, buildId);
            build.setCommiters(commiters);
            
        } catch (BuildNotFoundException e) {
            LOG.warn("BuildId " + buildId + " not found in software to update project " + project, e);
            //TODO remove buildId from buildIds as its removed from software
        }
    }

    public String[] updateStatusAndReturnBuildsToUpdate(Project project) throws ProjectNotFoundException,
            BuildNotFoundException {
        try {
            String lastBuildId = project.getBuildConnection().getLastBuildId(project.getBuildProjectId());
            boolean lastBuilding = project.getBuildConnection().isBuilding(project.getBuildProjectId(),
                    lastBuildId);

            String previousLastBuildId = project.getLastBuildId();
            boolean previousBuilding = false;
            try {
                previousBuilding = project.getLastBuild().isBuilding();
            } catch (BuildNotFoundException e) {
                LOG.debug("No lastBuild found to say the project was building before refresh " + project);
            }

            Build lastBuild = project.findCreatedBuild(lastBuildId);

            try {
                if (previousBuilding == false && lastBuilding == false && previousLastBuildId != lastBuildId) {
                    LOG.info("there is an already finished new build {}  {}", lastBuildId, project);
                    return new String[] { lastBuildId };
                }
                if (previousBuilding == false && lastBuilding == true) {
                    LOG.info("Build {} is now running {}", lastBuild.getBuildId(), project);
                    Runnable finishTimeRunner = getEstimatedFinishTimeRunner(project, lastBuild);
                    scheduler.schedule(finishTimeRunner, new Date());
                }
                if (previousBuilding == true && lastBuilding == true) {
                    if (previousLastBuildId != lastBuildId) {
                        LOG.info("Previous build {} is over and a new build {} is already running {}", new Object[] {
                                previousLastBuildId, lastBuildId, project });
                        project.getBuilds().get(previousLastBuildId).setEstimatedFinishTime(null);
                        project.getBuilds().get(previousLastBuildId).setBuilding(false);
                        Runnable finishTimeRunner = getEstimatedFinishTimeRunner(project, lastBuild);
                        scheduler.schedule(finishTimeRunner, new Date());
                        return new String[] { previousLastBuildId };
                    }
                    // building is still running
                }

                if (previousBuilding == true && lastBuilding == false) {
                    // build is over
                    project.getBuilds().get(previousLastBuildId).setEstimatedFinishTime(null);
                    project.getBuilds().get(previousLastBuildId).setBuilding(false);
                    if (lastBuildId != previousLastBuildId) {
                        LOG.info("previous build {} is over and a new build {} is also over {}", new Object[] {
                                previousLastBuildId, lastBuildId, project });
                        return new String[] { previousLastBuildId, lastBuildId };
                    }
                    LOG.info("Previous build {} is over and no new build ", previousLastBuildId, project);
                    return new String[] { previousLastBuildId };
                }
            } finally {
                lastBuild.setBuilding(lastBuilding);
                project.setLastBuildId(lastBuildId);
            }
        } catch (BuildIdNotFoundException e) {
            LOG.debug("No last build id found to update project " + project);
        }
        return new String[] {};
    }

    ////////////////////////////////////////////////////////////////////////

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    Runnable getEstimatedFinishTimeRunner(final Project project, final Build build) throws ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {
            @Override
            public void run() {
                LOG.debug("Running getEstimatedFinishTime for project " + project);
                try {
                    Date estimatedFinishTime = project.getBuildConnection().getEstimatedFinishTime(
                            project.getBuildProjectId(), build.getBuildId());
                    if (estimatedFinishTime != null) {
                        build.setEstimatedFinishTime(estimatedFinishTime);
                    }
                } catch (ProjectNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                } catch (BuildNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

}
