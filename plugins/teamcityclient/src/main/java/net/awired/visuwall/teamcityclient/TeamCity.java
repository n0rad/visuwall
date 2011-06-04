package net.awired.visuwall.teamcityclient;

import java.util.ArrayList;
import java.util.List;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class TeamCity {

    @VisibleForTesting
    TeamCityJerseyClient teamcityJerseyClient;

    public List<String> findProjectNames() {
        List<String> projectNames = new ArrayList<String>();
        List<TeamCityProject> projects = teamcityJerseyClient.getProjects();
        for (TeamCityProject project : projects) {
            projectNames.add(project.name);
        }
        return projectNames;
    }

    public List<TeamCityProject> findAllProjects() {
        return teamcityJerseyClient.getProjects();
    }

    public TeamCityProject findProject(String projectId) {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
        return teamcityJerseyClient.getProject(projectId);
    }

}
