package net.awired.visuwall.plugin.hudson;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;

import com.google.common.base.Preconditions;

public class ProjectBuilder {

    public Project buildProjectFrom(HudsonProject hudsonProject) {
        Preconditions.checkNotNull(hudsonProject, "hudsonProject is mandatory");

        Project project = new Project();
        project.setName(hudsonProject.getName());
        project.setDescription(hudsonProject.getDescription());
        project.setBuildNumbers(hudsonProject.getBuildNumbers());

        addCurrentAndCompletedBuilds(project, hudsonProject);

        return project;
    }

    public void addCurrentAndCompletedBuilds(Project project, HudsonProject hudsonProject) {
        HudsonBuild completedBuild = hudsonProject.getCompletedBuild();
        if (completedBuild != null) {
            project.setCompletedBuild(buildBuildFrom(completedBuild));
        }
        HudsonBuild currentBuild = hudsonProject.getCurrentBuild();
        if (currentBuild != null) {
            project.setCurrentBuild(buildBuildFrom(currentBuild));
        }
    }

    public Build buildBuildFrom(HudsonBuild hudsonBuild) {
        Preconditions.checkNotNull(hudsonBuild, "hudsonBuild");
        Build build = new Build();
        build.setCommiters(hudsonBuild.getCommiters());
        build.setDuration(hudsonBuild.getDuration());
        build.setStartTime(hudsonBuild.getStartTime());
        build.setTestResult(hudsonBuild.getTestResult());
        build.setBuildNumber(hudsonBuild.getBuildNumber());

        String hudsonBuildState = hudsonBuild.getState();
        build.setState(State.getStateByName(hudsonBuildState));

        return build;
    }
}
