package net.awired.visuwall.server.domain;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.service.BuildService;
import net.awired.visuwall.api.service.QualityService;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;

@Entity
public final class Wall {

    private static final int PROJECT_NOT_BUILT_ID = -1;

    private static final Logger LOG = LoggerFactory.getLogger(Wall.class);

    @Transient
    private Set<BuildService> buildServices = new HashSet<BuildService>();

    @Transient
    private Set<QualityService> qualityServices = new HashSet<QualityService>();

    @Transient
    private Map<ProjectId, ServiceHolder> projects = new HashMap<ProjectId, ServiceHolder>();

    @Transient
    private Map<String, ProjectId> projectIdsByProjectName = new HashMap<String, ProjectId>();

    @Id
    private String name;

    @OneToMany(cascade=CascadeType.ALL, fetch=FetchType.EAGER)
    private List<SoftwareAccess> softwareAccesses = new ArrayList<SoftwareAccess>();

    public Wall() { }

    public Wall(String name) {
        this.name = name;
    }

    public void addSoftwareAccess(SoftwareAccess softwareAccess) {
        this.softwareAccesses.add(softwareAccess);
        addServiceFrom(softwareAccess);
    }

    public void restoreServices() {
        for (SoftwareAccess access:softwareAccesses) {
            addServiceFrom(access);
        }
    }

    private void addServiceFrom(SoftwareAccess softwareAccess) {
        if (softwareAccess.getSoftware().isBuildSoftware()) {
            buildServices.add(softwareAccess.createBuildService());
        }
        if (softwareAccess.getSoftware().isQualitySoftware()) {
            qualityServices.add(softwareAccess.createQualityService());
        }
    }

    public void discoverProjects() {
        for(BuildService buildService:buildServices) {
            List<ProjectId> discoveredProjects = buildService.findAllProjects();
            for(ProjectId discoveredProject:discoveredProjects) {
                ServiceHolder holder = getServiceHolder(discoveredProject);
                holder.getBuildServices().add(buildService);
                for(QualityService qualityService:qualityServices) {
                    if(qualityService.contains(discoveredProject)) {
                        holder = getServiceHolder(discoveredProject);
                        holder.getQualityServices().add(qualityService);
                    }
                }
                projectIdsByProjectName.put(discoveredProject.getName(), discoveredProject);
                projects.put(discoveredProject, holder);
            }
        }
    }

    private ServiceHolder getServiceHolder(ProjectId discoveredProject) {
        ServiceHolder holder = projects.get(discoveredProject);
        if (holder == null) {
            holder = new ServiceHolder();
            projects.put(discoveredProject, holder);
        }
        return holder;
    }

    /**
     * @return null if no date could be estimated
     * @throws ProjectNotFoundException
     */
    public Date getEstimatedFinishTime(String projectName) throws ProjectNotFoundException {
        ProjectId projectId = projectIdsByProjectName.get(projectName);
        for(BuildService service:buildServices) {
            try {
                Date estimatedFinishTime = service.getEstimatedFinishTime(projectId);
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

    public Project findFreshProject(String projectName) throws ProjectNotFoundException {
        ProjectId projectId = projectIdsByProjectName.get(projectName);
        if (projectId == null) {
            throw new ProjectNotFoundException("Project [name="+projectName+"] has not been found.");
        }
        Project project = new Project();
        project.setProjectId(projectId);
        for(BuildService service:buildServices) {
            hydrateProjectFromBuildServices(project, service);
        }
        for(QualityService service:qualityServices) {
            hydrateProjectFromQualityServices(project, service);
        }

        if (project.getName() != null) {
            return project;
        }
        throw new ProjectNotFoundException("Project [projectId="+projectId+"] has no name.");
    }

    private void hydrateProjectFromQualityServices(Project projectToHydrate, QualityService service) {
        QualityResult qualityResult = service.populateQuality(projectToHydrate.getProjectId(), "coverage");
        if (qualityResult != null) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("service - "+service.getClass().getSimpleName());
            }
            for (Entry<String, QualityMeasure> entry : qualityResult.getMeasures()) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(entry.getKey()+" - "+entry.getValue());
                }
                projectToHydrate.getQualityResult().add(entry.getKey(), entry.getValue());
            }
        }
    }

    private void hydrateProjectFromBuildServices(Project projectToHydrate, BuildService service) {
        try {
            Project project = service.findProject(projectToHydrate.getProjectId());
            if (project != null) {
                String projectName = project.getName();
                String description = project.getDescription();
                String artifactId = project.getArtifactId();
                int[] buildNumbers = project.getBuildNumbers();
                Build completedBuild = project.getCompletedBuild();
                Build currentBuild = project.getCurrentBuild();
                State state = project.getState();

                if (LOG.isDebugEnabled()) {
                    LOG.debug("service - "+service.getClass().getSimpleName());
                    LOG.debug("projectName:"+ projectName);
                    LOG.debug("description:"+ description);
                    LOG.debug("artifactId:" +artifactId);
                    LOG.debug("buildNumbers:"+ Arrays.toString(buildNumbers));
                    LOG.debug("completedBuild:"+ completedBuild);
                    LOG.debug("currentBuild: "+currentBuild);
                    LOG.debug("state:"+ state);
                }

                if (StringUtils.isNotBlank(artifactId)) {
                    projectToHydrate.setArtifactId(artifactId);
                }
                if (buildNumbers != null) {
                    projectToHydrate.setBuildNumbers(buildNumbers);
                }
                if (completedBuild != null) {
                    projectToHydrate.setCompletedBuild(completedBuild);
                }
                if (currentBuild != null) {
                    projectToHydrate.setCurrentBuild(currentBuild);
                }
                if (StringUtils.isNotBlank(description)) {
                    projectToHydrate.setDescription(description);
                }
                if (StringUtils.isNotBlank(projectName)) {
                    projectToHydrate.setName(projectName);
                }
                if (state != null) {
                    projectToHydrate.setState(state);
                }
            }
        } catch(ProjectNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage());
            }
        }
    }

    public List<ProjectStatus> getStatus() {
        List<ProjectStatus> statusList = new ArrayList<ProjectStatus>();
        for (ProjectId projectId:projectIdsByProjectName.values()) {
            ProjectStatus status = new ProjectStatus();
            status.setBuilding(isBuilding(projectId));
            status.setLastBuildId(getLastBuildNumber(projectId));
            status.setName(projectId.getName());
            status.setState(getState(projectId));
            statusList.add(status);
        }
        return statusList;
    }

    private int getLastBuildNumber(ProjectId projectId) {
        for (BuildService service:buildServices) {
            try {
                return service.getLastBuildNumber(projectId);
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

    private State getState(ProjectId projectId) {
        for (BuildService service:buildServices) {
            try {
                return service.getState(projectId);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        throw new RuntimeException("Project must have a state.");
    }

    private boolean isBuilding(ProjectId projectId) {
        for (BuildService service:buildServices) {
            try {
                return service.isBuilding(projectId);
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        return false;
    }

    public Build findBuildByBuildNumber(String projectName, int buildNumber) throws BuildNotFoundException {
        ProjectId projectId = projectIdsByProjectName.get(projectName);
        for (BuildService service:buildServices) {
            try {
                Build build = service.findBuildByBuildNumber(projectId, buildNumber);
                if (build != null) {
                    return build;
                }
            } catch (BuildNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            } catch (ProjectNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug(e.getMessage());
                }
            }
        }
        throw new BuildNotFoundException("No build #"+buildNumber+" for project "+projectId);
    }

    public void setSoftwareAccesses(List<SoftwareAccess> softwareAccesses) {
        this.softwareAccesses = softwareAccesses;
    }

    public List<SoftwareAccess> getSoftwareAccesses() {
        return softwareAccesses;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
        .toString();
    }

    public Collection<Project> getProjects() {
        List<Project> allProjects = new ArrayList<Project>();
        for(ProjectId projectId:projects.keySet()) {
            try {
                allProjects.add(findFreshProject(projectId.getName()));
            } catch (ProjectNotFoundException e) {
                LOG.warn(e.getMessage());
            }
        }
        return allProjects;
    }

}
