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
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;
import junit.framework.Assert;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.hudsonclient.builder.HudsonBuildBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.builder.TestResultBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonView;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import net.awired.visuwall.hudsonclient.util.ClasspathFiles;
import org.junit.Before;
import org.junit.Test;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

@SuppressWarnings("unchecked")
public class HudsonFinderTest {

    HudsonUrlBuilder hudsonUrlBuilder;
    TestResultBuilder testResultBuilder;
    GenericSoftwareClient client;
    HudsonBuildBuilder hudsonBuildBuilder;

    static HudsonMavenMavenModuleSetBuild moduleSetBuild = mock(HudsonMavenMavenModuleSetBuild.class);
    static HudsonMavenReportersSurefireAggregatedReport surefireReport = mock(HudsonMavenReportersSurefireAggregatedReport.class);

    HudsonFinder hudsonFinder;

    @Before
    public void init() {
        hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        testResultBuilder = mock(TestResultBuilder.class);
        client = mock(GenericSoftwareClient.class);
        hudsonBuildBuilder = mock(HudsonBuildBuilder.class);
        hudsonFinder = new HudsonFinder(hudsonUrlBuilder);
        hudsonFinder.client = client;
        hudsonFinder.hudsonBuildBuilder = hudsonBuildBuilder;
        hudsonFinder.testResultBuilder = testResultBuilder;
    }

    @Test
    public void testFindCommiters() throws ResourceNotFoundException {
        HudsonUser user = new HudsonUser();
        user.setId("jsmadja");
        user.setName("Julien Smadja");
        user.setEmail("jsmadja@xebia.fr");

        when(client.resource(anyString(), any(Class.class))).thenReturn(user);

        Set<HudsonCommiter> commiters = hudsonFinder.findCommiters(new String[] { "Julien Smadja" });

        HudsonCommiter commiter = commiters.iterator().next();
        assertEquals("jsmadja", commiter.getId());
        assertEquals("Julien Smadja", commiter.getName());
        assertEquals("jsmadja@xebia.fr", commiter.getEmail());
    }

    @Test
    public void testFindJobNames() throws ResourceNotFoundException {
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

        when(client.resource(anyString(), any(Class.class))).thenReturn(jobs);

        List<String> projectNames = hudsonFinder.findJobNames();
        Assert.assertEquals("project1", projectNames.get(0));
        Assert.assertEquals("project2", projectNames.get(1));
    }

    @Test
    public void should_return_all_views() throws ResourceNotFoundException {
        HudsonModelHudson viewsResource = (HudsonModelHudson) load("hudson/views.xml", HudsonModelHudson.class);
        when(client.resource(anyString(), any(Class.class))).thenReturn(viewsResource);
        List<String> views = hudsonFinder.findViews();
        assertEquals(4, views.size());
        List<String> expectedViews = Arrays.asList("android", "on", "on-tools", "synthesis");
        for (String expectedView : expectedViews) {
            assertTrue(views.contains(expectedView));
        }
    }

    @Test
    public void should_return_all_projects_of_a_view() throws Exception {
        HudsonView viewResource = (HudsonView) load("hudson/view.xml", HudsonView.class);
        when(client.resource(anyString(), any(Class.class))).thenReturn(viewResource);
        List<String> projectNames = hudsonFinder.findJobNamesByView("android");
        assertEquals(4, projectNames.size());
        List<String> expectedProjects = Arrays.asList("android-1.11", "android-1.11-daily", "android-trunk",
                "android-trunk-daily");
        for (String expectedProject : expectedProjects) {
            assertTrue(projectNames.contains(expectedProject));
        }
    }

    private Object load(String fileName, Class<?> clazz) {
        try {
            String file = ClasspathFiles.getAbsolutePathFile(fileName);
            URL url = new URL(file);
            JAXBContext newInstance = JAXBContext.newInstance(clazz);
            Unmarshaller unmarshaller = newInstance.createUnmarshaller();
            return unmarshaller.unmarshal(url);
        } catch (Exception t) {
            throw new RuntimeException(t);
        }
    }

}
