package net.awired.visuwall.plugin.hudson;

import static org.apache.commons.lang.StringUtils.isBlank;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.hudsonclient.Hudson;
import net.awired.visuwall.hudsonclient.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public final class HudsonPlugin implements BuildPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonPlugin.class);

    private static final String HUDSON_ID = "HUDSON_ID";

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
    public List<ProjectId> findAllProjects() {
        List<ProjectId> projects = new ArrayList<ProjectId>();
        for(HudsonProject hudsonProject:hudson.findAllProjects()) {
            try {
                Project project = createProject(hudsonProject);
                ProjectId projectId = new ProjectId();
                projectId.setName(project.getName());
                projectId.addId(HUDSON_ID, project.getName());
                projects.add(projectId);
            } catch (HudsonProjectNotFoundException e) {
                LOG.warn(e.getMessage(), e);
            }
        }
        return projects;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId");
        try {
            String projectName = projectId.getId(HUDSON_ID);
            HudsonProject hudsonProject = hudson.findProject(projectName);
            Project project = createProject(hudsonProject);
            project.setProjectId(projectId);
            return project;
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

    private void addState(Project project, HudsonBuild build) {
        if (build == null) {
            project.setState(State.NEW);
        } else {
            project.setState(State.valueOf(build.getState()));
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
        project.setBuildNumbers(hudsonProject.getBuildNumbers());
        addCompletedBuild(project, hudsonProject);
        addCurrentBuild(project, hudsonProject);
        addState(project, hudsonProject);
        return project;
    }

    private void addState(Project project, HudsonProject hudsonProject) throws HudsonProjectNotFoundException {
        String projectName = hudsonProject.getName();
        String state = hudson.getState(projectName);
        project.setState(State.valueOf(state));
    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        try {
            String projectName = projectId.getId(HUDSON_ID);
            return hudson.getEstimatedFinishTime(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        try {
            String projectName = projectId.getId(HUDSON_ID);
            return hudson.isBuilding(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public State getState(ProjectId projectId) throws ProjectNotFoundException {
        try {
            String projectName = projectId.getId(HUDSON_ID);
            String state = hudson.getState(projectName);
            return State.valueOf(state);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
        try {
            String projectName = projectId.getId(HUDSON_ID);
            return  hudson.getLastBuildNumber(projectName);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException, ProjectNotFoundException {
        try {
            String projectName = projectId.getId(HUDSON_ID);
            HudsonBuild build = hudson.findBuild(projectName, buildNumber);
            return createBuild(build);
        } catch (HudsonBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        } catch (HudsonProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
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
