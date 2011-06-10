package net.awired.visuwall.core.domain;

import java.util.ArrayList;
import java.util.List;
import javax.persistence.Transient;
import org.codehaus.jackson.annotate.JsonIgnore;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.plugin.ConnectionPlugin;

public class ConnectedProject extends Project {

    @Transient
    @JsonIgnore
    private List<ConnectionPlugin> connectionPlugins = new ArrayList<ConnectionPlugin>();

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

}
