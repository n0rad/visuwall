package net.awired.visuwall.core.business.domain;

import java.util.ArrayList;
import java.util.List;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class ProjectHolder extends ArrayList<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectHolder.class);

    private static final long serialVersionUID = 1L;

    public List<SoftwareProjectId> getBuildProjectIds() {
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>(this.size());
        for (Project project : this) {
            projectIds.add(project.getBuildProjectId());
        }
        return projectIds;
    }

    public Project getById(String id) throws ProjectNotFoundException {
        Preconditions.checkNotNull(id, "projectId is mandatory");
        for (Project project : this) {
            if (id.equals(project.getId())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("Project not found for this id : " + id);
    }

    public void deleteAndCleanProject(String id) {
        try {
            Project project = this.getById(id);
            project.close();
            this.remove(project);
        } catch (ProjectNotFoundException e) {
            LOG.warn("Project with id " + id + " not found to be remove");
        }
    }
}
