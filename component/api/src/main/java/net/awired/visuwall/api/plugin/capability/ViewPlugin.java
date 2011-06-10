package net.awired.visuwall.api.plugin.capability;

import java.util.List;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.NotImplementedOperationException;
import net.awired.visuwall.api.exception.ViewNotFoundException;

public interface ViewPlugin {
    /**
     * Return a list of project id contained in the software by view names
     * 
     * @return
     */
    List<ProjectId> findProjectsByViews(List<String> views) throws NotImplementedOperationException;

    /**
     * Software can sort projects by views, or graphically by tabs. If so, plugin can list these views.
     * 
     * @return List of view names
     */
    List<String> findViews() throws NotImplementedOperationException;

    /**
     * If software sorts its projects by view, you should be able to retrieve project names by view name
     * 
     * @param viewName
     * @return List of project names contained in view
     * @throws ViewNotFoundException
     */
    List<String> findProjectsByView(String viewName) throws NotImplementedOperationException, ViewNotFoundException;

}
