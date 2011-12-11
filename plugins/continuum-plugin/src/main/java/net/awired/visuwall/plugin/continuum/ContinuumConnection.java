package net.awired.visuwall.plugin.continuum;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.clients.continuum.Continuum;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;

import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;

public class ContinuumConnection implements BuildCapability {

    private Continuum continuum;

    private boolean connected;

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        return null;
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        continuum = new Continuum(url);
        connected = true;
    }

    @Override
    public void close() {
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        try {
            ProjectSummary project = findProject(softwareProjectId);
            return project.getDescription();
        } catch (Exception e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        try {
            ProjectSummary project = findProject(softwareProjectId);
            return project.getName();
        } catch (Exception e) {
            throw new ProjectNotFoundException(e);
        }
    }

    private ProjectSummary findProject(SoftwareProjectId softwareProjectId) throws Exception {
        String projectId = softwareProjectId.getProjectId();
        Integer id = Integer.valueOf(projectId);
        ProjectSummary project = continuum.findProject(id);
        return project;
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        try {
            String projectMavenId = projectKey.getMavenId();
            List<ProjectSummary> projects = continuum.findAllProjects();
            for (ProjectSummary project : projects) {
                String groupId = project.getGroupId();
                String artifactId = project.getArtifactId();
                String mavenId = groupId + ":" + artifactId;
                if (mavenId.equals(projectMavenId)) {
                    return new SoftwareProjectId(Integer.toString(project.getId()));
                }
            }
        } catch (Exception e) {
            throw new ProjectNotFoundException("Can't identify " + projectKey, e);
        }
        throw new ProjectNotFoundException("Can't identify " + projectKey);
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> projectIds = new HashMap<SoftwareProjectId, String>();
        List<ProjectSummary> projects;
        try {
            projects = continuum.findAllProjects();
            for (ProjectSummary project : projects) {
                SoftwareProjectId projectId = new SoftwareProjectId(Integer.toString(project.getId()));
                projectIds.put(projectId, project.getName());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return projectIds;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        return new ArrayList<Commiter>();
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        throw new BuildNotFoundException("Not implemented!");
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return new ArrayList<String>();
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return BuildState.UNKNOWN;
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return new Date();
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        throw new BuildIdNotFoundException("Not implemented!");
    }

}
