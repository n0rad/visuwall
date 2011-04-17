package net.awired.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.bambooclient.Bamboo;
import net.awired.visuwall.bambooclient.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.BambooProjectNotFoundException;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;

public class BambooPlugin implements BuildPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(BambooPlugin.class);

    private static final String BAMBOO_ID = "BAMBOO_ID";

    private String url;

    private Bamboo bamboo;

    private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

    static {
        STATE_MAPPING.put("Successful", State.SUCCESS);
        STATE_MAPPING.put("Failed", State.FAILURE);
    }

    @Override
    public void setLogin(String login) {
    }

    @Override
    public void setPassword(String password) {
    }

    @Override
    public void setUrl(String url) {
        this.url = url;
    }

    @Override
    public void init() {
        Preconditions.checkNotNull(url, "Use setUrl() before calling init method");
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
        String projectName = getProjectName(projectId);
        return createProject(bamboo.findProject(projectName));
    }

    private String getProjectName(ProjectId projectId) {
        return projectId.getId(BAMBOO_ID);
    }

    @Override
    public Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
    ProjectNotFoundException {
        String projectName = getProjectName(projectId);
        try {
            return createBuild(bamboo.findBuild(projectName, buildNumber));
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException(e);
        }
    }

    private Build createBuild(BambooBuild bambooBuild) {
        Build build = new Build();
        build.setBuildNumber(bambooBuild.getBuildNumber());
        build.setDuration(bambooBuild.getDuration());
        build.setStartTime(bambooBuild.getStartTime());
        build.setState(getState(bambooBuild.getState()));
        TestResult testResult = new TestResult();
        testResult.setFailCount(bambooBuild.getFailCount());
        testResult.setPassCount(bambooBuild.getPassCount());
        build.setTestResult(testResult);
        return build;
    }

    private State getState(String bambooState) {
        State state = STATE_MAPPING.get(bambooState);
        if (state == null) {
            throw new RuntimeException("No state mapping for bambooState: "+bambooState);
        }
        return state;
    }

    @Override
    public void populate(Project project) throws ProjectNotFoundException {
        Project bambooProject = findProject(project.getProjectId());
        project.setName(bambooProject.getName());
    }

    @Override
    public Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectName(projectId);
        try {
            return bamboo.getEstimatedFinishTime(projectName);
        } catch (BambooProjectNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectName(projectId);
        BambooProject bambooProject = bamboo.findProject(projectName);
        return bambooProject.isBuilding();
    }

    @Override
    public State getState(ProjectId projectId) throws ProjectNotFoundException {
        String projectName = getProjectName(projectId);
        String bambooState = bamboo.getState(projectName);
        return getState(bambooState);
    }

    @Override
    public int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId");
        String id = getProjectName(projectId);
        Preconditions.checkNotNull(id, BAMBOO_ID);

        return bamboo.getLastBuildNumber(id);
    }

}
