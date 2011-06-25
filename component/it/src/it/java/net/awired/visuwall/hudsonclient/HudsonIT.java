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

package net.awired.visuwall.hudsonclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import java.util.List;
import java.util.Set;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonJob;
import net.awired.visuwall.hudsonclient.domain.HudsonTestResult;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;

import org.junit.Test;

public class HudsonIT {

    private Hudson hudson = new Hudson(IntegrationTestData.HUDSON_URL);

    @Test
    public void should_find_not_built_project() throws HudsonJobNotFoundException {
        hudson.findJob("neverbuild");
    }

    @Test
    public void should_count_it_and_ut() throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        HudsonBuild build = hudson.findBuild("itcoverage-project", 17);
        HudsonTestResult unitTestResult = build.getUnitTestResult();
        HudsonTestResult integrationTestResult = build.getIntegrationTestResult();

        assertEquals(1, unitTestResult.getFailCount());
        assertEquals(5, unitTestResult.getSkipCount());
        assertEquals(3, unitTestResult.getPassCount());
        assertEquals(9, unitTestResult.getTotalCount());

        assertEquals(4, integrationTestResult.getFailCount());
        assertEquals(6, integrationTestResult.getSkipCount());
        assertEquals(2, integrationTestResult.getPassCount());
        assertEquals(12, integrationTestResult.getTotalCount());
    }

    @Test
    public void should_not_find_non_maven_projects() throws Exception {
        List<HudsonJob> projects = hudson.findAllProjects();
        for (HudsonJob project : projects) {
            if ("freestyle-project".equals(project.getName())) {
                fail(project.getName() + " is not a maven project");
            }
        }
    }

    @Test
    public void should_retrieve_commiter_email() throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        Hudson hudsonAwired = new Hudson(IntegrationTestData.JENKINS_URL);
        HudsonBuild build = hudsonAwired.findBuild("successproject", 9);
        Set<HudsonCommiter> set = build.getCommiters();
        HudsonCommiter commiter = set.iterator().next();

        assertEquals("Arnaud LEMAIRE", commiter.getId());
        assertEquals("Arnaud LEMAIRE", commiter.getName());
        assertEquals("alemaire@norad.fr", commiter.getEmail());
    }
}
