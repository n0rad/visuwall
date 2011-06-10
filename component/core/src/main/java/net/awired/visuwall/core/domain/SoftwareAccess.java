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

import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.Lob;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.awired.visuwall.api.domain.Project;

import org.hibernate.annotations.Cascade;
import org.hibernate.annotations.CollectionOfElements;
import org.hibernate.annotations.Type;
import org.springframework.util.AutoPopulatingList;

@Entity
public class SoftwareAccess {

	@Id
	@GeneratedValue
	private Long id;

	private String url;
	private String login;
	private String password;

	@Transient
	private String pluginClassName;

	private boolean allProject;

	@CollectionOfElements
	private List<String> projectNames = new AutoPopulatingList<String>(
			String.class);

	@CollectionOfElements
	private List<String> viewNames = new AutoPopulatingList<String>(
			String.class);

	public SoftwareAccess() {

	}

	public SoftwareAccess(Class<?> plugin, String url) {
		this.pluginClassName = plugin.getName();
		this.url = url;
	}

	public SoftwareAccess(Class<?> plugin, String url, String login,
			String password) {
		this(plugin, url);
		this.login = login;
		this.password = password;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		SoftwareAccess other = (SoftwareAccess) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	// ///////////////////////////////////////////////////////////

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getLogin() {
		return login;
	}

	public void setLogin(String login) {
		this.login = login;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getPluginClassName() {
		return pluginClassName;
	}

	public void setPluginClassName(String pluginClassName) {
		this.pluginClassName = pluginClassName;
	}

	public boolean isAllProject() {
		return allProject;
	}

	public void setAllProject(boolean allProject) {
		this.allProject = allProject;
	}

	public List<String> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(List<String> projectNames) {
		this.projectNames = projectNames;
	}

	public List<String> getViewNames() {
		return viewNames;
	}

	public void setViewNames(List<String> viewNames) {
		this.viewNames = viewNames;
	}
}
