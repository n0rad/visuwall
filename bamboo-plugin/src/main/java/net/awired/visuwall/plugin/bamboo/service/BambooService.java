package net.awired.visuwall.plugin.bamboo.service;

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
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.domain.BambooProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class BambooService implements BuildPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(BambooService.class);

    private static final String BAMBOO_ID = "BAMBOO_ID";

    private String url;
    private String login;
    private String password;

    private Bamboo bamboo;

    @Override
    public void setLogin(String login) {
        this.login = login;
    }

    @Override
    public void setPassword(String password) {
        this.password = password;
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void init() {
        Preconditions.checkNotNull(url, "url");
        bamboo = new Bamboo(url);
    }

    @Override
    public List<ProjectId> findAllProjects() {
        List<ProjectId> projects = new ArrayList<ProjectId>();
        for(BambooProject bambooProject:bamboo.findAllProjects()) {
            ProjectId projectId = new ProjectId();
            projectId.setName(bambooProject.getName());
            projectId.addId(BAMBOO_ID, bambooProject.getKey());
            projects.add(projectId);
        }
        return projects;
    }

    private Project createProject(BambooProject bambooProject) {
        Project project = new Project();
        project.setName(bambooProject.getName());
        return project;
    }

    @Override
    public Project findProject(ProjectId projectId) throws ProjectNotFoundException {
        return createProject(bamboo.findProject(projectId.getId(BAMBOO_ID)));
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
    ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void populate(Project project) throws ProjectNotFoundException {
        // TODO Auto-generated method stub

    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public State getState(ProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
        // TODO Auto-generated method stub
        return 0;
    }

}
