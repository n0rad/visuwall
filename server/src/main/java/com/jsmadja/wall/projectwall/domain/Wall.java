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
import com.jsmadja.wall.projectwall.service.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.Service;


public class Wall {

    private static final int PROJECT_NOT_BUILT_ID = -1;

    private static final Logger LOG = LoggerFactory.getLogger(Wall.class);

    private Set<Service> services = new HashSet<Service>();

    private Set<Project> projects = new HashSet<Project>();

    private String name;

    public Wall(String name) {
        this.name = name;
    }

    public void addSoftwareAccess(SoftwareAccess softwareAccess) {
        services.add(softwareAccess.createService());
    }

    public void refreshProjects() {
        synchronized (projects) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Refreshing projects");
            }
            projects.clear();
            for(Service service:services) {
                projects.addAll(service.findAllProjects());
            }
            populate(projects);
        }
    }

    public Collection<Project> findAllProjects() {
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
        for(Service service:services) {
            try {
                service.populate(project);
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
        for(Service service:services) {
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
        for(Service service:services) {
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
        for (Service service:services) {
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
        for (Service service:services) {
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
        for (Service service:services) {
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
        for (Service service:services) {
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
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("name", name) //
        .add("projects", Arrays.toString(projects.toArray())) //
        .toString();
    }
}
