package net.awired.visuwall.plugin.hudson.service;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.service.BuildService;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public final class HudsonService implements BuildService {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonService.class);

    private String url;
    private String login;
    private String password;

    private Hudson hudson;

    @Override
    public void init() {
        if (isBlank(url)) {
            throw new IllegalStateException("url can't be null.");
        }
        hudson = new Hudson(url);
    }

    @Override
    public List<Project> findAllProjects() {
        List<Project> projects = new ArrayList<Project>();
        for(HudsonProject hudsonProject:hudson.findAllProjects()) {
            Project project;
            try {
                project = createProject(hudsonProject);
                projects.add(project);
            } catch (HudsonProjectNotFoundException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return projects;
    }

    @Override
    public Project findProjectByName(String projectName) throws ProjectNotFoundException {
        try {
            HudsonProject hudsonProject = hudson.findProject(projectName);
            return createProject(hudsonProject);
        } catch(HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public void populate(Project project) throws ProjectNotFoundException {
        try {
            HudsonProject hudsonProject = hudson.findProject(project.getName());
            HudsonBuild completedBuild = addCompletedBuild(project, hudsonProject);
            HudsonBuild lastBuild = completedBuild;
            addCurrentBuild(project, hudsonProject);
            addState(project, lastBuild);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    private void addState(Project project, HudsonBuild lastBuild) {
        if (lastBuild == null) {
            project.setState(State.NEW);
        } else {
            project.setState(State.valueOf(lastBuild.getState()));
        }
    }

    private HudsonBuild addCompletedBuild(Project project, HudsonProject hudsonProject) {
        HudsonBuild completedBuild = hudsonProject.getCompletedBuild();
        if (completedBuild != null) {
            project.setCompletedBuild(createBuild(completedBuild));
        }
        return completedBuild;
    }

    private void addCurrentBuild(Project project, HudsonProject hudsonProject) {
        HudsonBuild currentBuild = hudsonProject.getCurrentBuild();
        if (currentBuild != null) {
            project.setCurrentBuild(createBuild(currentBuild));
        }
    }

    private Project createProject(HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        Project project = new Project();
        project.setName(hudsonProject.getName());
        project.setDescription(hudsonProject.getDescription());
        project.setId(hudsonProject.getArtifactId());
        project.setBuildNumbers(hudsonProject.getBuildNumbers());
        addCompletedBuild(project, hudsonProject);
        addCurrentBuild(project, hudsonProject);
        return project;
    }

    @Override
    public Date getEstimatedFinishTime(String projectName) throws ProjectNotFoundException {
        try {
            return hudson.getEstimatedFinishTime(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public boolean isBuilding(String projectName) throws ProjectNotFoundException {
        try {
            return hudson.isBuilding(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public State getState(String projectName) throws ProjectNotFoundException {
        try {
            String state = hudson.getState(projectName);
            return State.valueOf(state);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public int getLastBuildNumber(String projectName) throws ProjectNotFoundException, BuildNotFoundException {
        try {
            return  hudson.getLastBuildNumber(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public Build findBuildByProjectNameAndBuildNumber(String projectName, int buildNumber) throws BuildNotFoundException {
        try {
            HudsonBuild build = hudson.findBuild(projectName, buildNumber);
            return createBuild(build);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    private Build createBuild(HudsonBuild hudsonBuild) {
        Preconditions.checkNotNull(hudsonBuild, "hudsonBuild");
        Build build = new Build();
        build.setCommiters(hudsonBuild.getCommiters());
        build.setDuration(hudsonBuild.getDuration());
        build.setStartTime(hudsonBuild.getStartTime());
        build.setState(State.valueOf(hudsonBuild.getState()));
        build.setTestResult(hudsonBuild.getTestResult());
        build.setBuildNumber(hudsonBuild.getBuildNumber());
        return build;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

}
