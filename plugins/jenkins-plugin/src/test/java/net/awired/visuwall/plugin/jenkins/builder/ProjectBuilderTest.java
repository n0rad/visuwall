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

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.plugin.jenkins.JenkinsState;

import org.junit.Test;

public class ProjectBuilderTest {

    private ProjectBuilder projectBuilder = new ProjectBuilder();

    @Test
    public void should_add_current_build_and_completed_build() {
        HudsonCommiter commiterInCurrentBuild = new HudsonCommiter("commiter1");
        HudsonCommiter commiterInCompletedBuild = new HudsonCommiter("commiter2");

        HudsonBuild currentBuild = new HudsonBuild();
        currentBuild.getCommiters().add(commiterInCurrentBuild);
		currentBuild.setState(JenkinsState.SUCCESS);

        HudsonBuild completedBuild = new HudsonBuild();
        completedBuild.getCommiters().add(commiterInCompletedBuild);
		completedBuild.setState(JenkinsState.SUCCESS);

        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setCurrentBuild(currentBuild);
        hudsonProject.setCompletedBuild(completedBuild);

        Project project = new Project("");
        projectBuilder.addCurrentAndCompletedBuilds(project, hudsonProject);

        Commiter createdCommiterInCurrentBuild = project.getCurrentBuild().getCommiters().iterator().next();
        Commiter createdCommiterInCompletedBuild = project.getCompletedBuild().getCommiters().iterator().next();

        assertEquals(commiterInCurrentBuild.getName(), createdCommiterInCurrentBuild.getName());
        assertEquals(commiterInCompletedBuild.getName(), createdCommiterInCompletedBuild.getName());
    }

    @Test
    public void should_build_valid_project() {
        HudsonProject hudsonProject = new HudsonProject();
        hudsonProject.setName("name");
        int[] buildNumbers = new int[] { 1, 2, 3 };
        hudsonProject.setBuildNumbers(buildNumbers);

        Project project = projectBuilder.buildProjectFrom(hudsonProject);

        assertEquals(hudsonProject.getName(), project.getName());
        assertArrayEquals(buildNumbers, project.getBuildNumbers());
    }
}
