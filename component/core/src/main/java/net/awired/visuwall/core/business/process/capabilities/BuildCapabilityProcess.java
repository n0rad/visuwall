package net.awired.visuwall.core.business.process.capabilities;

import java.util.Date;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.core.business.domain.Build;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.stereotype.Component;
import com.google.common.base.Preconditions;

@Component
public class BuildCapabilityProcess {

    @Autowired
    TaskScheduler scheduler;

    private static final Logger LOG = LoggerFactory.getLogger(BuildCapabilityProcess.class);

    public boolean updateStatusAndReturnFullUpdateNeeded(ConnectedProject project) throws ProjectNotFoundException {
        try {
            int lastBuildNumber = project.getBuildConnection().getLastBuildNumber(project.getBuildProjectId());
            int previousLastBuildNumber = project.getCurrentBuildId();
            boolean building = project.getBuildConnection().isBuilding(project.getBuildProjectId());
            boolean previousBuilding = false;
            try {
                previousBuilding = project.getCurrentBuild().isBuilding();
            } catch (BuildNotFoundException e) {
                LOG.info("No currentBuild found to say the project was building before refresh " + project);
            }

            Build lastBuild = project.findCreatedBuild(lastBuildNumber);
            lastBuild.setBuilding(building);
            project.setCurrentBuildId(lastBuildNumber);
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

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    Runnable getEstimatedFinishTimeRunner(final ConnectedProject project, final Build build)
            throws ProjectNotFoundException {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        return new Runnable() {
            @Override
            public void run() {
                LOG.info("Running getEstimatedFinishTime for project " + project);
                try {
                    Date estimatedFinishTime = project.getBuildConnection().getEstimatedFinishTime(
                            project.getBuildProjectId());
                    if (estimatedFinishTime != null) {
                        build.setEstimatedFinishTime(estimatedFinishTime);
                    }
                } catch (ProjectNotFoundException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        };
    }

}
