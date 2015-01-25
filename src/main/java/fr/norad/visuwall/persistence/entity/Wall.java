/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.persistence.entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import org.hibernate.annotations.Cascade;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import fr.norad.visuwall.domain.Project;
import fr.norad.visuwall.domain.ProjectHolder;
import fr.norad.visuwall.plugin.capability.BasicCapability;
import net.awired.ajsl.persistence.entity.IdEntityImpl;

@Entity
@NamedQueries({
        @NamedQuery(name = Wall.QUERY_NAMES, query = "SELECT name FROM Wall"), //
        @NamedQuery(name = Wall.QUERY_WALLS, query = "SELECT w FROM Wall AS w"), //
        @NamedQuery(name = Wall.QUERY_WALLBYNAME, query = "select w FROM Wall AS w where w.name = :"
                + Wall.QUERY_PARAM_NAME) })
public final class Wall extends IdEntityImpl<Long> {
    public static final String QUERY_NAMES = "wallNames";
    public static final String QUERY_WALLS = "walls";
    public static final String QUERY_WALLBYNAME = "wallByName";
    public static final String QUERY_PARAM_NAME = "wallName";

    private static final Logger LOG = LoggerFactory.getLogger(Wall.class);

    private static final long serialVersionUID = 1L;

    @NotNull
    @Size(min = 1)
    @Column(nullable = false, unique = true)
    private String name;

    @Valid
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "WALL_ID", nullable = false)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.EVICT,
            org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<SoftwareAccess> softwareAccesses = new ArrayList<>(); // new PopulatingShrinkList<SoftwareAccess>(SoftwareAccess.class);

    @Transient
    private final ProjectHolder projects = new ProjectHolder();

    public Wall() {
    }

    public void close() {
        for (Project project : getProjects()) {
            project.close();
        }
        for (SoftwareAccess softwareAccess : softwareAccesses) {
            ScheduledFuture<?> projectFinderTask = softwareAccess.getProjectFinderTask();
            if (projectFinderTask != null) {
                projectFinderTask.cancel(true);
            }
            BasicCapability connection = softwareAccess.getConnection();
            if (connection != null) {
                try {
                    connection.close();
                } catch (Exception e) {
                    LOG.warn("can not close softwareAccess connection", e);
                }
            }
        }
    }

    public Wall(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Wall)) {
            return false;
        }
        return name == ((Wall) obj).name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", name) //
                // .add("projects", Arrays.toString(projects.toArray())) //
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

    public ProjectHolder getProjects() {
        return projects;
    }

}
