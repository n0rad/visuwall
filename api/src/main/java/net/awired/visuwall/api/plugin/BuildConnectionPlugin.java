package net.awired.visuwall.api.plugin;

import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;


public interface BuildConnectionPlugin extends ConnectionPlugin {

    List<ProjectId> findAllProjects();

    Project findProject(ProjectId projectId) throws ProjectNotFoundException;

    Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException, ProjectNotFoundException;

    /**
     * Populating a project means fill its attributes with all data that you can fetch from your system
     * If you can fetch State or Build informations, add it in <code>project</code>!
     * @param project Project to populate
     * @throws ProjectNotFoundException Throw this exception if you can't find this project in your system
     */
    void populate(Project project) throws ProjectNotFoundException;

    Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException;

    boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException;

    State getState(ProjectId projectId) throws ProjectNotFoundException;

    int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException;

}