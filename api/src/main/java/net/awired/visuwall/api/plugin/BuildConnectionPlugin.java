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

    void populate(Project project) throws ProjectNotFoundException;

    Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException;

    boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException;

    State getState(ProjectId projectId) throws ProjectNotFoundException;

    int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException;

}