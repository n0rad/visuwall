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

package net.awired.visuwall.hudsonclient.finder;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import junit.framework.Assert;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.hudsonclient.HudsonJerseyClient;
import net.awired.visuwall.hudsonclient.builder.HudsonBuildBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.builder.TestResultBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonProjectNotFoundException;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.util.ClasspathFiles;

import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

public class HudsonFinderTest {

    HudsonUrlBuilder hudsonUrlBuilder;
    TestResultBuilder testResultBuilder;
    HudsonJerseyClient hudsonJerseyClient;
    HudsonBuildBuilder hudsonBuildBuilder;

    static HudsonMavenMavenModuleSetBuild moduleSetBuild = mock(HudsonMavenMavenModuleSetBuild.class);
    static HudsonMavenReportersSurefireAggregatedReport surefireReport = mock(HudsonMavenReportersSurefireAggregatedReport.class);

    HudsonFinder hudsonFinder;

    @Before
    public void init() {
        hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        testResultBuilder = mock(TestResultBuilder.class);
        hudsonJerseyClient = mock(HudsonJerseyClient.class);
        hudsonBuildBuilder = mock(HudsonBuildBuilder.class);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder, hudsonJerseyClient, hudsonBuildBuilder);
    }

    @Test
    public void testFindCommiters() {
        HudsonUser user = new HudsonUser();
        user.setId("jsmadja");
        user.setName("Julien Smadja");
        user.setEmail("jsmadja@xebia.fr");

        when(hudsonJerseyClient.getHudsonUser(anyString())).thenReturn(user);

        Set<Commiter> commiters = hudsonFinder.findCommiters(new String[] { "Julien Smadja" });

        Commiter commiter = commiters.iterator().next();
        assertEquals("jsmadja", commiter.getId());
        assertEquals("Julien Smadja", commiter.getName());
        assertEquals("jsmadja@xebia.fr", commiter.getEmail());
    }

    @Test
    public void testFind() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        when(hudsonJerseyClient.getModuleSetBuild(anyString())).thenReturn(moduleSetBuild);
        when(hudsonJerseyClient.getSurefireReport(anyString())).thenReturn(surefireReport);
        when(
                hudsonBuildBuilder.createHudsonBuild(any(HudsonMavenMavenModuleSetBuild.class),
                        any(HudsonMavenReportersSurefireAggregatedReport.class), any(Set.class))).thenReturn(
                new HudsonBuild());

        HudsonBuild hudsonBuild = hudsonFinder.find("projectName", 5);

        assertNotNull(hudsonBuild);
        verify(hudsonUrlBuilder).getBuildUrl(anyString(), anyInt());
        verify(hudsonJerseyClient).getModuleSetBuild(anyString());
    }

    @Test
    public void testFindProjectNames() {
        Element project1 = mock(Element.class);
        Node mock1 = mock(Node.class);
        when(project1.getFirstChild()).thenReturn(mock1);
        when(mock1.getFirstChild()).thenReturn(mock1);
        when(mock1.getNodeValue()).thenReturn("project1");

        Element project2 = mock(Element.class);
        Node mock2 = mock(Node.class);
        when(project2.getFirstChild()).thenReturn(mock2);
        when(mock2.getFirstChild()).thenReturn(mock2);
        when(mock2.getNodeValue()).thenReturn("project2");

        List<Object> elements = new ArrayList<Object>();
        elements.add(project1);
        elements.add(project2);

        HudsonModelHudson jobs = mock(HudsonModelHudson.class);
        when(jobs.getJob()).thenReturn(elements);

        when(hudsonJerseyClient.getHudsonJobs(anyString())).thenReturn(jobs);

        List<String> projectNames = hudsonFinder.findProjectNames();
        Assert.assertEquals("project1", projectNames.get(0));
        Assert.assertEquals("project2", projectNames.get(1));
    }

    @Test
    public void testProjectExists() {
        String absolutePathFile = ClasspathFiles.getAbsolutePathFile("hudson/fluxx.xml");
        when(hudsonUrlBuilder.getProjectUrl(anyString())).thenReturn(absolutePathFile);
        boolean found = hudsonFinder.projectExists("projectName");
        assertTrue(found);
    }

}
