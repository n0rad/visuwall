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

package net.awired.visuwall.core.domain;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.core.utils.ShrinkList;

import org.codehaus.jackson.annotate.JsonIgnore;
import org.hibernate.annotations.Cascade;
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
	@GeneratedValue(strategy = GenerationType.AUTO)
	private Long id;

	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	@JoinColumn(name = "WALL_ID", nullable = false)
	@Cascade({ org.hibernate.annotations.CascadeType.SAVE_UPDATE,
			org.hibernate.annotations.CascadeType.EVICT,
			org.hibernate.annotations.CascadeType.DELETE,
			org.hibernate.annotations.CascadeType.DELETE_ORPHAN })
	private List<SoftwareAccess> softwareAccesses = new ShrinkList<SoftwareAccess>(
			SoftwareAccess.class);

	@Transient
	private List<Project> projects = new AutoPopulatingList<Project>(Project.class);

	@Transient
	@JsonIgnore
	private List<ConnectionPlugin> connectionPlugin = new ArrayList<ConnectionPlugin>();

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

	public List<Project> getProjects() {
		return projects;
	}

	public void setProjects(List<Project> projects) {
		this.projects = projects;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	@JsonIgnore
	public List<ConnectionPlugin> getConnectionPlugin() {
		return connectionPlugin;
	}

	@JsonIgnore
	public void setConnectionPlugin(List<ConnectionPlugin> connectionPlugin) {
		this.connectionPlugin = connectionPlugin;
	}

}
