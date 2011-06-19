package net.awired.visuwall.core.business.process.capabilities;

import java.util.Arrays;
import java.util.Date;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import org.apache.commons.lang.StringUtils;
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

    private static final int PROJECT_NOT_BUILT_ID = -1;

    public boolean updateStatusAndReturnFullUpdateNeeded(ConnectedProject project) throws ProjectNotFoundException {
        try {
            int lastBuildNumber = project.getBuildConnection().getLastBuildNumber(project.getProjectId());
            int previousLastBuildNumber = project.getCurrentBuildId();
            boolean building = project.getBuildConnection().isBuilding(project.getProjectId());
            boolean previousBuilding = false;
            if (project.getCurrentBuild() != null) {
                previousBuilding = project.getCurrentBuild().isBuilding();
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
                lastBuild.setEstimatedFinishTime(null);
            }

            if (previousLastBuildNumber != lastBuildNumber && !building) {
                return true;
            }
            return false;
        } catch (BuildNotFoundException e) {
            // TODO should not throw a BuildNotFound I Think
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    /////////////////////////////////////

    void enhanceWithBuildInformations(Project projectToMerge, BasicCapability buildPlugin) {
        ProjectId projectId = projectToMerge.getProjectId();
        Preconditions.checkState(projectId != null, "projectToComplete must have a projectId");
        try {
            Project project = buildPlugin.findProject(projectId);
            if (project != null) {
                String projectName = project.getName();
                String description = project.getDescription();
                int[] buildNumbers = project.getBuildNumbers();
                Build completedBuild = project.getCompletedBuild();
                Build currentBuild = project.getCurrentBuild();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("plugin - " + buildPlugin.getClass().getSimpleName());
                    LOG.debug("projectName:" + projectName);
                    LOG.debug("description:" + description);
                    LOG.debug("buildNumbers:" + Arrays.toString(buildNumbers));
                    LOG.debug("completedBuild:" + completedBuild);
                    LOG.debug("currentBuild: " + currentBuild);
                }

                if (buildNumbers != null) {
                    projectToMerge.setBuildNumbers(buildNumbers);
                }
                if (completedBuild != null) {
                    projectToMerge.setCompletedBuild(completedBuild);
                }
                if (currentBuild != null) {
                    projectToMerge.setCurrentBuild(currentBuild);
                }
                if (StringUtils.isNotBlank(description)) {
                    projectToMerge.setDescription(description);
                }
                if (StringUtils.isNotBlank(projectName)) {
                    projectToMerge.setName(projectName);
                }
            }
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
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
                            project.getProjectId());
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

    Build findBuildByBuildNumber(ConnectedProject project, int buildNumber) throws BuildNotFoundException {
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

    public State getState(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project is a mandatory parameter");
        try {
            return project.getBuildConnection().getLastBuildState(project.getProjectId());
        } catch (ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
        throw new RuntimeException("Project " + project + " must have a state. It can't be found in ");
    }

}
