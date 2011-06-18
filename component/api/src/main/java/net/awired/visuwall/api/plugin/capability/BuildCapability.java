package net.awired.visuwall.api.plugin.capability;

import java.util.Date;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface BuildCapability extends BasicCapability {

    /**
     * Project are in a certain state which may vary between software You'll have to try to associate them with common
     * States
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    @Deprecated
    State getState(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Builds are in a certain state which may vary between software You'll have to try to associate them with common
     * States
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    State getLastBuildState(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Build software can order their builds by number, plugin should be able to retrieve builds by number too
     * 
     * @param projectId
     * @param buildNumber
     * @return
     * @throws BuildNotFoundException
     * @throws ProjectNotFoundException
     */
    Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException;

    /**
     * If a project is building, plugin can calculate the estimated finish time
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return true if project is building
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return the last build number of a project
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     * @throws BuildNotFoundException
     */
    int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException;

}
