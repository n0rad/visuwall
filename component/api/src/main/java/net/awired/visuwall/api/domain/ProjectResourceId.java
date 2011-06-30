package net.awired.visuwall.api.domain;

import java.util.Date;

public class ProjectResourceId {
    private SoftwareProjectId softwareProjectId;
    private Date date;
    private String version;
    private int buildNumber;

    public SoftwareProjectId getSoftwareProjectId() {
        return softwareProjectId;
    }

    public void setSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        this.softwareProjectId = softwareProjectId;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }
}
