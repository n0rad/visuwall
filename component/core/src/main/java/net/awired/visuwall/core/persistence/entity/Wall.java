/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.visuwall.core.persistence.entity;

import java.util.List;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;
import net.awired.ajsl.persistence.entity.implementation.abstracts.IdEntityImpl;
import net.awired.visuwall.core.business.domain.ConnectedProject;
import net.awired.visuwall.core.business.domain.ProjectHolder;
import net.awired.visuwall.core.utils.ShrinkList;
import org.hibernate.annotations.Cascade;
import com.google.common.base.Objects;

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

    private static final long serialVersionUID = 1L;

    @Column(nullable = false, unique = true)
    private String name;

    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    @JoinColumn(name = "WALL_ID", nullable = false)
    @Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE, org.hibernate.annotations.CascadeType.EVICT,
            org.hibernate.annotations.CascadeType.DELETE, org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
    private List<SoftwareAccess> softwareAccesses = new ShrinkList<SoftwareAccess>(SoftwareAccess.class);

    @Transient
    private final ProjectHolder projects = new ProjectHolder();

    public Wall() {
    }

    public void close() {
        for (ConnectedProject project : getProjects()) {
            project.close();
        }
        for (SoftwareAccess softwareAccess : softwareAccesses) {
            softwareAccess.getProjectFinderTask().cancel(true);
            softwareAccess.getConnection().close();
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
