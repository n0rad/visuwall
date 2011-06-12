package net.awired.visuwall.core.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.capability.BuildPlugin;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ConnectedProject extends Project {

    @Transient
    @JsonIgnore
    private List<ConnectionPlugin> connectionPlugins = new ArrayList<ConnectionPlugin>();

    @Transient
    @JsonIgnore
    private BuildPlugin buildPlugin;

    public ConnectedProject(String name) {
        super(name);
    }

    public ConnectedProject(ProjectId projectId) {
        super(projectId);
    }

    /////////////////////////////////

    @JsonIgnore
    public List<ConnectionPlugin> getConnectionPlugins() {
        return connectionPlugins;
    }

    @JsonIgnore
    public void setConnectionPlugins(List<ConnectionPlugin> connectionPlugins) {
        this.connectionPlugins = connectionPlugins;
    }

    @JsonIgnore
    public BuildPlugin getBuildPlugin() {
        return buildPlugin;
    }

    @JsonIgnore
    public void setBuildPlugin(BuildPlugin buildPlugin) {
        this.buildPlugin = buildPlugin;
    }

}
