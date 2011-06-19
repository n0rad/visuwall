package net.awired.visuwall.api.domain;

public class SoftwareProjectId<ID_TYPE> {

    private ID_TYPE projectId;

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((projectId == null) ? 0 : projectId.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        SoftwareProjectId other = (SoftwareProjectId) obj;
        if (projectId == null) {
            if (other.projectId != null) {
                return false;
            }
        } else if (!projectId.equals(other.projectId)) {
            return false;
        }
        return true;
    }

    //////////////////////////////////////////////////////////////

    public void setProjectId(ID_TYPE projectId) {
        this.projectId = projectId;
    }

    public ID_TYPE getProjectId() {
        return projectId;
    }
}
