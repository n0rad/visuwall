package net.awired.visuwall.core.domain;

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.springframework.util.AutoPopulatingList;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

@Entity
@NamedQueries({ @NamedQuery(name = Wall.QUERY_NAMES, query = "SELECT name FROM Wall"), //
    @NamedQuery(name = Wall.QUERY_WALLS, query = "SELECT w FROM Wall AS w") })
    public final class Wall {

    public static final String QUERY_NAMES = "wallNames";
    public static final String QUERY_WALLS = "walls";

    @Id
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<SoftwareAccess> softwareAccesses = new AutoPopulatingList<SoftwareAccess>(SoftwareAccess.class);

    @Transient
    private List<Project> projects = new AutoPopulatingList<Project>(Project.class);

    @Transient
    transient private PluginHolder pluginHolder;

    public Wall() {
    }

    public Wall(String name) {
        this.name = name;
    }

    public Project getProjectByProjectId(ProjectId projectId) throws ProjectNotFoundException {
        Preconditions.checkNotNull(projectId, "projectId is mandatory");
        for (Project project : projects) {
            if (projectId.equals(project.getProjectId())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("project with this id not found : " + projectId);
    }

    public Project getProjectByName(String name) throws ProjectNotFoundException {
        Preconditions.checkNotNull(name, "name is mandatory");
        for (Project project : projects) {
            if (name.equals(project.getName())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("Project not found for this name : " + name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Wall))
            return false;
        return name == ((Wall) obj).name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("name", name) //
        .toString();
    }

    // /////////////////////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<SoftwareAccess> getSoftwareAccesses() {
        return softwareAccesses;
    }

    public void setSoftwareAccesses(List<SoftwareAccess> softwareAccesses) {
        this.softwareAccesses = softwareAccesses;
    }

    public List<Project> getProjects() {
        return projects;
    }

    public void setProjects(List<Project> projects) {
        this.projects = projects;
    }

    public PluginHolder getPluginHolder() {
        return pluginHolder;
    }

    public void setPluginHolder(PluginHolder pluginHolder) {
        this.pluginHolder = pluginHolder;
    }

}
