package net.awired.visuwall.api.service;

import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;


public interface BuildService extends Service {

    List<Project> findAllProjects();

    Project findProjectByName(String projectName) throws ProjectNotFoundException;

    Build findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber) throws BuildNotFoundException, ProjectNotFoundException;

    void populate(Project project) throws ProjectNotFoundException;

    Date getEstimatedFinishTime(String projectName) throws ProjectNotFoundException;

    boolean isBuilding(String projectName) throws ProjectNotFoundException;

    State getState(String projectName) throws ProjectNotFoundException;

    int getLastBuildNumber(String projectName) throws ProjectNotFoundException, BuildNotFoundException;

}