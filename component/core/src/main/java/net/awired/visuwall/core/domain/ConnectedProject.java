package net.awired.visuwall.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.Connection;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ConnectedProject extends Project {

    private boolean building = false;

    @Transient
    private List<Connection> connectionPlugins = new ArrayList<Connection>();
    @Transient
    private BuildCapability buildPlugin;
    @Transient
    private ScheduledFuture<Object> projectStatusTask;

    public void close() {
        projectStatusTask.cancel(true);
    }

    public ConnectedProject(String name) {
        super(name);
    }

    public ConnectedProject(ProjectId projectId) {
        super(projectId);
    }

    /////////////////////////////////

    @JsonIgnore
    public List<Connection> getConnectionPlugins() {
        return connectionPlugins;
    }

    @JsonIgnore
    public void setConnectionPlugins(List<Connection> connectionPlugins) {
        this.connectionPlugins = connectionPlugins;
    }

    @JsonIgnore
    public BuildCapability getBuildPlugin() {
        return buildPlugin;
    }

    @JsonIgnore
    public void setBuildPlugin(BuildCapability buildPlugin) {
        this.buildPlugin = buildPlugin;
    }

    @JsonIgnore
    public void setProjectStatusTask(ScheduledFuture<Object> projectStatusTask) {
        this.projectStatusTask = projectStatusTask;
    }

    @JsonIgnore
    public ScheduledFuture<Object> getProjectStatusTask() {
        return projectStatusTask;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public boolean isBuilding() {
        return building;
    }

}
