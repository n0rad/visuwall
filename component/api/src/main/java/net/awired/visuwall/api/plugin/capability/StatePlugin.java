package net.awired.visuwall.api.plugin.capability;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface StatePlugin {

    /**
     * Project are in a certain state which may vary between software You'll have to try to associate them with common
     * States
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    State getState(ProjectId projectId) throws NotImplementedOperationException, ProjectNotFoundException;

}
