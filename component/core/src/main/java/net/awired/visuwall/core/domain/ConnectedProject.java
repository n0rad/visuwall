package net.awired.visuwall.core.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ConnectedProject extends Project {

    @Transient
    private List<BasicCapability> capabilities = new ArrayList<BasicCapability>();
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
    public void setCapabilities(List<BasicCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonIgnore
    public List<BasicCapability> getCapabilities() {
        return capabilities;
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

}
