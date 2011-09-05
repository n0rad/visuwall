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

package net.awired.clients.jenkins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import net.awired.clients.common.Tests;
import net.awired.clients.hudson.domain.HudsonBuild;
import net.awired.clients.hudson.domain.HudsonCommiter;
import net.awired.clients.hudson.domain.HudsonJob;
import net.awired.clients.hudson.exception.HudsonBuildNotFoundException;
import net.awired.clients.hudson.exception.HudsonJobNotFoundException;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class JenkinsITest {

    private Jenkins jenkins;

    @Parameters
    public static Collection<Object[]> createParameters() {
        String instanceProperty = "jenkinsInstances";
        return Tests.createUrlInstanceParametersFromProperty(instanceProperty);
    }

    public JenkinsITest(String jenkinsUrl) {
        jenkins = new Jenkins(jenkinsUrl);
    }

    @Test
    public void should_find_not_built_project() throws HudsonJobNotFoundException {
        jenkins.findJob("neverbuild");
    }

    @Test
    public void should_not_find_non_maven_projects() throws Exception {
        List<HudsonJob> projects = jenkins.findAllProjects();
        for (HudsonJob project : projects) {
            if ("freestyle-project".equals(project.getName())) {
                fail(project.getName() + " is not a maven project");
            }
        }
    }

    @Test
    public void should_retrieve_commiter_email() throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        HudsonBuild build = jenkins.findBuild("successproject", 13);
        Set<HudsonCommiter> set = build.getCommiters();
        HudsonCommiter commiter = set.iterator().next();

        assertEquals("Arnaud LEMAIRE", commiter.getId());
        assertEquals("Arnaud LEMAIRE", commiter.getName());
        assertEquals("alemaire@norad.fr", commiter.getEmail());
    }

    @Test
    public void should_find_synthesis_root_module_from_jenkins() throws Exception {
        JenkinsUrlBuilder jenkinsUrlBuilder = new JenkinsUrlBuilder(jenkins.getUrl());
        JenkinsRootModuleFinder jenkinsRootModuleFinder = new JenkinsRootModuleFinder(jenkinsUrlBuilder);
        String artifactId = jenkinsRootModuleFinder.findArtifactId("struts 2 instable");
        Assert.assertEquals("org.apache.struts:struts2-parent", artifactId);
    }
}
