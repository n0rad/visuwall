package com.jsmadja.wall.projectwall.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.jsmadja.wall.projectwall.service.BuildNotFoundException;
import com.jsmadja.wall.projectwall.service.BuildService;
import com.jsmadja.wall.projectwall.service.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.QualityService;


public class Wall {

    private static final int PROJECT_NOT_BUILT_ID = -1;

    private static final Logger LOG = LoggerFactory.getLogger(Wall.class);

    private Set<BuildService> buildServices = new HashSet<BuildService>();
    private Set<QualityService> qualityServices = new HashSet<QualityService>();

    private Set<Project> projects = new HashSet<Project>();

    private String name;

    public Wall(String name) {
        this.name = name;
    }

    public void addSoftwareAccess(SoftwareAccess softwareAccess) {
        if (softwareAccess.getSoftware().isBuildSoftware()) {
            buildServices.add(softwareAccess.createBuildService());
        }
        if (softwareAccess.getSoftware().isQualitySoftware()) {
            qualityServices.add(softwareAccess.createQualityService());
        }
    }

    public void refreshProjects() {
        synchronized (projects) {
            projects = new HashSet<Project>();
            for(BuildService service:buildServices) {
                projects.addAll(service.findAllProjects());
            }
            populate(projects);
        }
    }

    public Collection<Project> getProjects() {
        return projects;
    }

    private void populate(Collection<Project> projects) {
        for (Project project:projects) {
            populate(project);
        }
    }

    public void populate(Project project) {
        QualityResult qualityResult = project.getQualityResult();
        if (qualityResult == null) {
            qualityResult = new QualityResult();
            project.setQualityResult(qualityResult);
        }
        for(BuildService service:buildServices) {
            try {
                service.populate(project);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        for(QualityService service:qualityServices) {
            try {
                service.populateQuality(project, qualityResult, "violations_density", "technical_debt_days");
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
    }

    /**
     * @param projectName
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    public Date getEstimatedFinishTime(String projectName) throws ProjectNotFoundException {
        Project project = findProjectByName(projectName);
        for(BuildService service:buildServices) {
            try {
                Date estimatedFinishTime = service.getEstimatedFinishTime(project);
                if (estimatedFinishTime != null) {
                    return estimatedFinishTime;
                }
            } catch(ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return null;
    }

    public Project findProjectByName(String projectName) throws ProjectNotFoundException {
        for(BuildService service:buildServices) {
            try {
                Project project = service.findProjectByName(projectName);
                if (project != null) {
                    populate(project);
                    synchronized (projects) {
                        projects.add(project);
                    }
                    return project;
                }
            } catch(ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        throw new ProjectNotFoundException("Project [name="+projectName+"] has not been found.");
    }

    public List<ProjectStatus> getStatus() {
        synchronized (projects) {
            List<ProjectStatus> statusList = new ArrayList<ProjectStatus>();
            for (Project project:projects) {
                ProjectStatus status = new ProjectStatus();
                status.setBuilding(isBuilding(project));
                status.setLastBuildId(getLastBuildNumber(project));
                status.setName(project.getName());
                status.setState(getState(project));
                statusList.add(status);
            }
            return statusList;
        }
    }

    private int getLastBuildNumber(Project project) {
        for (BuildService service:buildServices) {
            try {
                return service.getLastBuildNumber(project);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (BuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return PROJECT_NOT_BUILT_ID;
    }

    private State getState(Project project) {
        for (BuildService service:buildServices) {
            try {
                return service.getState(project);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        throw new RuntimeException("Project must have a state."+project);
    }

    private boolean isBuilding(Project project) {
        for (BuildService service:buildServices) {
            try {
                return service.isBuilding(project);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return false;
    }

    public Build findBuildByProjectNameAndBuilderNumber(String projectName, int buildNumber) throws BuildNotFoundException {
        for (BuildService service:buildServices) {
            try {
                Build build = service.findBuildByProjectNameAndBuildNumber(projectName, buildNumber);
                if (build != null) {
                    return build;
                }
            } catch (BuildNotFoundException e) {
                if (LOG.isInfoEnabled()) {
                    LOG.info(e.getMessage());
                }
            }
        }
        throw new BuildNotFoundException("No build #"+buildNumber+" for project "+projectName);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Wall))
            return false;
        return name == ((Wall)obj).name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("name", name) //
        .add("projects", Arrays.toString(projects.toArray())) //
        .toString();
    }
}
