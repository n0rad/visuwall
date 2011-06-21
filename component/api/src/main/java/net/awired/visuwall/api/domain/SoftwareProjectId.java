package net.awired.visuwall.api.domain;

import com.google.common.base.Objects;

public class SoftwareProjectId {

    private String projectId;

    public SoftwareProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoftwareProjectId) {
            SoftwareProjectId p = (SoftwareProjectId) obj;
            return Objects.equal(projectId, p.projectId);
        }
        return false;
    }

    //////////////////////////////////////////////////////////////

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
