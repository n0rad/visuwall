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

    private static final int SKIP_FIRST_BUILD_POS = 1;

    public void updatePreviousCompletedBuild(Project project) throws ProjectNotFoundException {
        List<Integer> buildNumbers = project.getBuildNumbers();
        if (buildNumbers.size() < 2) {
            return;
        }

        List<Integer> previousBuilds = buildNumbers.subList(1, buildNumbers.size() - 1);
        for (Integer buildNumber : previousBuilds) {
            Build build = project.getBuilds().get(buildNumber);
            if (build == null) {
                updateBuild(project, buildNumber);
                build = project.getBuilds().get(buildNumber);
                if (build == null) {
                    LOG.warn("Build " + buildNumber + " not found after update for project " + project);
                    continue;
                }
            }

            State state = build.getState();
            if (state == State.UNKNOWN || state == State.ABORTED || state == State.NOTBUILT) {
                continue;
            }

            project.setLastCompletedBuildNumber(build.getBuildNumber());
            break;
        }
    }

    public void updateBuild(Project project, Integer buildNumber) throws ProjectNotFoundException {
        Preconditions.checkState(!project.getBuildNumbers().contains(buildNumber),
                "buildNumber '%s' not found in builds Number to update build in project %s ", buildNumber, project);
        try {
            SoftwareProjectId projectId = project.getBuildProjectId();

            State state = project.getBuildConnection().getBuildState(projectId, buildNumber);
            boolean building = project.getBuildConnection().isBuilding(projectId, buildNumber);
            // buildTime
            //TODO it

            Build lastBuild = project.findCreatedBuild(buildNumber);
            lastBuild.setState(state);
            lastBuild.setBuilding(building);

            if (building == true) {
                Date estimatedFinishTime = project.getBuildConnection()
                        .getEstimatedFinishTime(projectId, buildNumber);
                lastBuild.setEstimatedFinishTime(estimatedFinishTime);
            }

            project.findCreatedBuild(buildNumber);
        } catch (BuildNotFoundException e) {
            LOG.warn("BuildNumber " + buildNumber + " not found in software to update project " + project, e);
            //TODO remove buildNumber from buildNumbers as its removed from software
        }
    }

    public boolean updateStatusAndReturnFullUpdateNeeded(Project project) throws ProjectNotFoundException,
            BuildNotFoundException {
        try {
            int lastBuildNumber = project.getBuildConnection().getLastBuildNumber(project.getBuildProjectId());
            int previousLastBuildNumber = project.getLastBuildNumber();
            boolean building = project.getBuildConnection().isBuilding(project.getBuildProjectId(), lastBuildNumber);
            boolean previousBuilding = false;
            //TODO
            //            try {
            previousBuilding = project.getLastBuild().isBuilding();
            //            } catch (BuildNotFoundException e) {
            //                LOG.info("No currentBuild found to say the project was building before refresh " + project);
            //            }

            Build lastBuild = project.findCreatedBuild(lastBuildNumber);
            lastBuild.setBuilding(building);
            project.setLastBuildNumber(lastBuildNumber);
            if (previousBuilding == false && building == true) {
                // currently building
                Runnable finishTimeRunner = getEstimatedFinishTimeRunner(project, lastBuild);
                scheduler.schedule(finishTimeRunner, new Date());
            }
            if (previousBuilding == true && building == false) {
                // build is over
                lastBuild.setEstimatedFinishTime(null);
            }

            if (previousLastBuildNumber != lastBuildNumber && !building) {
                return true;
            }
        } catch (BuildNumberNotFoundException e) {
            LOG.info("No last build number found to update project " + project);
        }
        return false;
    }

    ////////////////////////////////////////////////////////////////////////

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    Runnable getEstimatedFinishTimeRunner(final Project project, final Build build)
            throws ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {
            @Override
            public void run() {
                LOG.info("Running getEstimatedFinishTime for project " + project);
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
