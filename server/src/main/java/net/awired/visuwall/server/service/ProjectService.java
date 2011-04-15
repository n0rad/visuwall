package net.awired.visuwall.server.service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.api.plugin.QualityPlugin;
import net.awired.visuwall.server.domain.ServiceHolder;
import net.awired.visuwall.server.domain.SoftwareAccess;
import net.awired.visuwall.server.domain.Wall;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;

public class ProjectService {

	private static final Logger LOG = LoggerFactory.getLogger(ProjectService.class);
	
//	@Transient
//	private Map<ProjectId, ServiceHolder> projects = new ConcurrentHashMap<ProjectId, ServiceHolder>();

	
    private static final int PROJECT_NOT_BUILT_ID = -1;

    private Set<BuildPlugin> buildServices = new HashSet<BuildPlugin>();

    private Set<QualityPlugin> qualityServices = new HashSet<QualityPlugin>();

    private Map<String, ProjectId> projectIdsByProjectName = new HashMap<String, ProjectId>();

    private ProjectMergeService pluginMergeService = new ProjectMergeService();
    
	public Collection<Project> getProjects(Wall wall) {
		List<Project> allProjects = new ArrayList<Project>();
		for (ProjectId projectId : projects.keySet()) {
			try {
				allProjects.add(findFreshProject(projectId.getName()));
			} catch (ProjectNotFoundException e) {
				LOG.warn(e.getMessage());
			}
		}
		return allProjects;
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
        for(BuildPlugin buildService : buildServices) {
            List<ProjectId> discoveredProjects = buildService.findAllProjects();
            for(ProjectId discoveredProject:discoveredProjects) {
                ServiceHolder holder = getServiceHolder(discoveredProject);
                holder.getBuildServices().add(buildService);
                for(QualityPlugin qualityService:qualityServices) {
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
        for(BuildPlugin service:buildServices) {
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
        for(BuildPlugin service:buildServices) {
            pluginMergeService.merge(project, service);
        }
        for(QualityPlugin service:qualityServices) {
            pluginMergeService.merge(project, service, "coverage");
        }

        if (LOG.isDebugEnabled()) {
            LOG.debug(project.toString());
        }

        if (project.getName() != null) {
            return project;
        }
        projectIdsByProjectName.remove(projectName);
        projects.remove(projectId);
        throw new ProjectNotFoundException("Project [projectId="+projectId+"] has no name.");
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
        for (BuildPlugin service:buildServices) {
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
        for (BuildPlugin service:buildServices) {
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
        for (BuildPlugin service:buildServices) {
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
        for (BuildPlugin service:buildServices) {
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
	////////
	//////////
	////////////
	////////////
    private final static int EVERY_FIVE_MINUTES = 5*60*1000;

    @Scheduled(fixedDelay=EVERY_FIVE_MINUTES)
    public void refreshWalls() {
        if (LOG.isInfoEnabled()) {
            LOG.info("It's time to refresh all walls");
        }
        for(Wall wall : WALLS.values()) {
            if (LOG.isInfoEnabled()) {
                LOG.info("Refreshing wall : "+wall+" and its "+wall.getProjects().size()+" projects");
            }
            wall.discoverProjects();
        }
    }

    public List<ProjectStatus> getStatus(String wallName) {
        Wall wall = WALLS.get(wallName);
        List<ProjectStatus> projectStatus  = wall.getStatus();
        return projectStatus;
    }

}
