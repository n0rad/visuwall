/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.api.domain;

import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.quality.QualityResult;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Project implements Comparable<Project> {

	private ProjectId projectId;
	private String description;
	private QualityResult qualityResult = new QualityResult();
	private State state;
	private int[] buildNumbers;

	private Build completedBuild;
	private Build currentBuild;

	public Project(String name) {
		Preconditions.checkNotNull(name, "name is a mandatory parameter");
		projectId = new ProjectId();
		projectId.setName(name);
	}

	public Project(ProjectId projectId) {
		Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");
		this.projectId = projectId;
	}

	// ////////////////////////////////////////////////////////////////

	public String getName() {
		return projectId.getName();
	}

	public void setName(String name) {
		projectId.setName(name);
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public ProjectId getProjectId() {
		return projectId;
	}

	public QualityResult getQualityResult() {
		return qualityResult;
	}

	public State getState() {
		return state;
	}

	public void setState(State state) {
		this.state = state;
	}

	public int[] getBuildNumbers() {
		return buildNumbers;
	}

	public void setBuildNumbers(int[] buildNumbers) {
		this.buildNumbers = buildNumbers;
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(projectId);
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Project)) {
			return false;
		}

		if (projectId == null) {
			return false;
		}

		Project project = (Project) obj;
		return projectId.equals(project.projectId);
	}

	public Build getCompletedBuild() {
		return completedBuild;
	}

	public void setCompletedBuild(Build completedBuild) {
		this.completedBuild = completedBuild;
	}

	public Build getCurrentBuild() {
		return currentBuild;
	}

	public void setCurrentBuild(Build currentBuild) {
		this.currentBuild = currentBuild;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
		        .add("project id", projectId) //
		        .add("name", getName()) //
		        .add("description", description) //
		        .add("state", state) //
		        .add("completed build", completedBuild) //
		        .add("quality result", qualityResult) //
		        .toString();
	}

	@Override
	public int compareTo(Project project) {
		Preconditions.checkNotNull(project, "project");
		return getName().compareTo(project.getName());
	}

	public void addId(String idName, String idValue) {
		projectId.addId(idName, idValue);
	}

}
