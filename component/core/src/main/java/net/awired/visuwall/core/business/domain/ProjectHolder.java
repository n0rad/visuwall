package net.awired.visuwall.core.business.domain;

import java.util.ArrayList;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class ProjectHolder extends ArrayList<ConnectedProject> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectHolder.class);

    private static final long serialVersionUID = 1L;

    public boolean containsId(ProjectId projectId) {
        for (Project project : this) {
            if (project.getProjectId().equals(projectId)) {
                return true;
            }
        }
        return false;
    }

    public ConnectedProject getByProjectId(ProjectId projectId) throws ProjectNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
        for (ConnectedProject project : this) {
            if (projectId.equals(project.getProjectId())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("project with this id not found : " + projectId);
    }

    public ConnectedProject getById(String id) throws ProjectNotFoundException {
        Preconditions.checkNotNull(id, "projectId is mandatory");
        for (ConnectedProject project : this) {
            if (id.equals(project.getId())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("Project not found for this id : " + id);
    }

    public void deleteAndCleanProject(String id) {
        try {
            ConnectedProject project = this.getById(id);
            project.close();
            this.remove(project);
        } catch (ProjectNotFoundException e) {
            LOG.warn("Project with id " + id + " not found to be remove");
        }
    }
}
