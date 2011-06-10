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

package net.awired.visuwall.hudsonclient.builder;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.net.URL;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonProject;
import net.awired.visuwall.hudsonclient.finder.HudsonFinder;
import net.awired.visuwall.hudsonclient.finder.HudsonRootModuleFinder;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.util.ClasspathFiles;
import org.junit.Test;

public class HudsonProjectBuilderTest {

    @Test
    public void should_create_valid_hudson_project() throws Exception {
        HudsonRootModuleFinder hudsonRootModuleFinder = mock(HudsonRootModuleFinder.class);
        when(hudsonRootModuleFinder.findArtifactId(anyString())).thenReturn("fr.kaddath.apps.fluxx:fluxx");

        HudsonFinder hudsonFinder = mock(HudsonFinder.class);
        HudsonBuild hudsonBuild = new HudsonBuild();
        when(hudsonFinder.find(anyString(), anyInt())).thenReturn(hudsonBuild);

        HudsonUrlBuilder hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        HudsonProjectBuilder hudsonProjectBuilder = new HudsonProjectBuilder(hudsonUrlBuilder, hudsonFinder);
        hudsonProjectBuilder.setHudsonRootModuleFinder(hudsonRootModuleFinder);

        HudsonMavenMavenModuleSet moduleSet = createModuleSetFrom("fluxx.xml");
        HudsonProject hudsonProject = hudsonProjectBuilder.createHudsonProjectFrom(moduleSet);

        assertEquals("fluxx", hudsonProject.getName());
        assertEquals("Fluxx, aggrégez vos flux RSS!", hudsonProject.getDescription());
        assertEquals("fr.kaddath.apps.fluxx:fluxx", hudsonProject.getArtifactId());
        assertArrayEquals(new int[] { 102, 101 }, hudsonProject.getBuildNumbers());
        assertEquals(hudsonBuild, hudsonProject.getCompletedBuild());
        assertEquals(hudsonBuild, hudsonProject.getCurrentBuild());
    }

    private HudsonMavenMavenModuleSet createModuleSetFrom(String fileName) throws Exception {
        String file = ClasspathFiles.getAbsolutePathFile("hudson/" + fileName);
        URL url = new URL(file);
        JAXBContext newInstance = JAXBContext.newInstance(HudsonMavenMavenModuleSet.class);
        Unmarshaller unmarshaller = newInstance.createUnmarshaller();
        JAXBElement<HudsonMavenMavenModuleSet> element = (JAXBElement<HudsonMavenMavenModuleSet>) unmarshaller
                .unmarshal(url);
        return element.getValue();
    }
}
