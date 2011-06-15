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

package net.awired.visuwall.plugin.jenkins.builder;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;

import com.google.common.base.Preconditions;

public class ProjectBuilder {

	private BuildBuilder buildBuilder = new BuildBuilder();

	public Project buildProjectFrom(HudsonProject hudsonProject) {
		checkHudsonProject(hudsonProject);
        Project project = new Project(hudsonProject.getName());
        project.setBuildNumbers(hudsonProject.getBuildNumbers());
        addCurrentAndCompletedBuilds(project, hudsonProject);
        return project;
    }

    public void addCurrentAndCompletedBuilds(Project project, HudsonProject hudsonProject) {
		Preconditions.checkNotNull(project, "project is mandatory");
		checkHudsonProject(hudsonProject);
        HudsonBuild completedBuild = hudsonProject.getCompletedBuild();
        if (completedBuild != null) {
        	Build build = buildBuilder .createBuildFrom(completedBuild);
            project.setCompletedBuild(build);
        }
        HudsonBuild currentBuild = hudsonProject.getCurrentBuild();
        if (currentBuild != null) {
			Build build = buildBuilder.createBuildFrom(currentBuild);
            project.setCurrentBuild(build);
        }
    }

	private void checkHudsonProject(HudsonProject hudsonProject) {
		Preconditions.checkNotNull(hudsonProject, "hudsonProject is mandatory");
	}
}
