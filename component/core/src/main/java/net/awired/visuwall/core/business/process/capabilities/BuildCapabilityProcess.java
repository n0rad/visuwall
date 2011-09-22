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
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
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
        List<Integer> buildNumbers = project.getBuildNumbers();
        if (buildNumbers.size() < 2) {
            // with only a build there is no previous completed build
            return;
        }
        try {
            int skip = 1;
            if (project.getLastBuild().isBuilding()) {
                if (buildNumbers.size() < 3) {
                    // first build is building so we do not have enough builds to have a previous completed 
                    return;
                }
                skip = 2;
            }

            ListIterator<Integer> reverseBuildIt = project.getBuildNumbers().listIterator(
                    project.getBuildNumbers().size());

            for (int i = 0; reverseBuildIt.hasPrevious(); i++) {
                Integer buildNumber = reverseBuildIt.previous();
                if (i < skip) {
                    continue;
                }
                Build build = getCreatedWithContentBuild(project, buildNumber);
                if (build == null) {
                    continue;
                }
                State state = build.getState();
                if (state == State.UNKNOWN || state == State.ABORTED) {
                    continue;
                }

                project.setPreviousCompletedBuildNumber(build.getBuildNumber());
                break;
            }
        } catch (BuildNotFoundException e) {
            LOG.warn("last build not found to update previous completed build", e);
        }
    }

    private Build getCreatedWithContentBuild(Project project, int buildNumber) throws ProjectNotFoundException {
        Build build = project.getBuilds().get(buildNumber);
        if (build == null) {
            updateBuild(project, buildNumber);
            build = project.getBuilds().get(buildNumber);
            if (build == null) {
                LOG.warn("Build " + buildNumber + " not found after update for project " + project);
            }
        }
        return build;
    }

    public void updateLastNotBuildingNumber(Project project) throws ProjectNotFoundException {
        ListIterator<Integer> reverseBuildIt = project.getBuildNumbers().listIterator(
                project.getBuildNumbers().size());
        while (reverseBuildIt.hasPrevious()) {
            Integer buildNumber = reverseBuildIt.previous();
            Build build = getCreatedWithContentBuild(project, buildNumber);
            if (build.isBuilding()) {
                continue;
            }
            project.setLastNotBuildingNumber(buildNumber);
            break;
        }
    }

    public void updateBuild(Project project, Integer buildNumber) throws ProjectNotFoundException {
        try {
            LOG.info("Updating build " + buildNumber + " for project " + project);
            SoftwareProjectId projectId = project.getBuildProjectId();
            Build build = project.findCreatedBuild(buildNumber);

            State state = project.getBuildConnection().getBuildState(projectId, buildNumber);
            build.setState(state);

            // buildTime
            BuildTime buildTime = project.getBuildConnection().getBuildTime(projectId, buildNumber);
            build.setStartTime(buildTime.getStartTime());
            build.setDuration(buildTime.getDuration());
            
            List<Commiter> commiters = project.getBuildConnection().getBuildCommiters(projectId, buildNumber);
            build.setCommiters(commiters);
            
            //            // projectSoftwareId
            //            ProjectResourceId projectResourceId = new ProjectResourceId();
            //            projectResourceId.setDate(new Date(build.getStartTime().getTime() + build.getDuration()));
            //            projectResourceId.setBuildNumber(build.getBuildNumber());

        } catch (BuildNotFoundException e) {
            LOG.warn("BuildNumber " + buildNumber + " not found in software to update project " + project, e);
            //TODO remove buildNumber from buildNumbers as its removed from software
        }
    }

    public int[] updateStatusAndReturnBuildsToUpdate(Project project) throws ProjectNotFoundException,
            BuildNotFoundException {
        try {
            int lastBuildNumber = project.getBuildConnection().getLastBuildNumber(project.getBuildProjectId());
            boolean lastBuilding = project.getBuildConnection().isBuilding(project.getBuildProjectId(),
                    lastBuildNumber);

            int previousLastBuildNumber = project.getLastBuildNumber();
            boolean previousBuilding = false;
            try {
                previousBuilding = project.getLastBuild().isBuilding();
            } catch (BuildNotFoundException e) {
                LOG.debug("No lastBuild found to say the project was building before refresh " + project);
            }

            Build lastBuild = project.findCreatedBuild(lastBuildNumber);

            try {
                if (previousBuilding == false && lastBuilding == false && previousLastBuildNumber != lastBuildNumber) {
                    LOG.info("there is an already finished new build {}  {}", lastBuildNumber, project);
                    return new int[] { lastBuildNumber };
                }
                if (previousBuilding == false && lastBuilding == true) {
                    LOG.info("Build {} is now running {}", lastBuild.getBuildNumber(), project);
                    Runnable finishTimeRunner = getEstimatedFinishTimeRunner(project, lastBuild);
                    scheduler.schedule(finishTimeRunner, new Date());
                }
                if (previousBuilding == true && lastBuilding == true) {
                    if (previousLastBuildNumber != lastBuildNumber) {
                        LOG.info("Previous build {} is over and a new build {} is already running {}", new Object[] {
                                previousLastBuildNumber, lastBuildNumber, project });
                        project.getBuilds().get(previousLastBuildNumber).setEstimatedFinishTime(null);
                        project.getBuilds().get(previousLastBuildNumber).setBuilding(false);
                        Runnable finishTimeRunner = getEstimatedFinishTimeRunner(project, lastBuild);
                        scheduler.schedule(finishTimeRunner, new Date());
                        return new int[] { previousLastBuildNumber };
                    }
                    // building is still running
                }

                if (previousBuilding == true && lastBuilding == false) {
                    // build is over
                    project.getBuilds().get(previousLastBuildNumber).setEstimatedFinishTime(null);
                    project.getBuilds().get(previousLastBuildNumber).setBuilding(false);
                    if (lastBuildNumber != previousLastBuildNumber) {
                        LOG.info("previous build {} is over and a new build {} is also over {}", new Object[] {
                                previousLastBuildNumber, lastBuildNumber, project });
                        return new int[] { previousLastBuildNumber, lastBuildNumber };
                    }
                    LOG.info("Previous build {} is over and no new build ", previousLastBuildNumber, project);
                    return new int[] { previousLastBuildNumber };
                }
            } finally {
                lastBuild.setBuilding(lastBuilding);
                project.setLastBuildNumber(lastBuildNumber);
            }
        } catch (BuildNumberNotFoundException e) {
            LOG.debug("No last build number found to update project " + project);
        }
        return new int[] {};
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
                            project.getBuildProjectId(), build.getBuildNumber());
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
