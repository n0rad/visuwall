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

package net.awired.clients.hudson;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.awired.clients.hudson.exception.ArtifactIdNotFoundException;
import net.awired.clients.hudson.exception.DocumentNotLoadedException;
import net.awired.clients.hudson.loader.DocumentLoader;
import net.awired.clients.hudson.util.ClasspathFiles;
import org.junit.Test;
import org.w3c.dom.Document;

public class HudsonRootModuleFinderTest {

    @Test
    public void should_find_artifact_id() throws Exception {
        String pomUrl = ClasspathFiles.getAbsolutePathFile("pom-sample.xml");
        Document document = new DocumentLoader().loadFromUrl(pomUrl);
        DocumentLoader documentLoader = mock(DocumentLoader.class);
        when(documentLoader.loadFromUrl(anyString())).thenReturn(document);

        HudsonUrlBuilder hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        HudsonRootModuleFinder hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
        hudsonRootModuleFinder.documentLoader = documentLoader;

        String artifactId = hudsonRootModuleFinder.findArtifactId("test-project");
        assertEquals("net.awired.visuwall:visuwall-hudsonclient", artifactId);
    }

    @Test(expected = ArtifactIdNotFoundException.class)
    public void should_not_find_artifact_id() throws Exception {
        DocumentLoader documentLoader = mock(DocumentLoader.class);
        when(documentLoader.loadFromUrl(anyString())).thenThrow(
                new DocumentNotLoadedException("file not found", null));

        HudsonUrlBuilder hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        HudsonRootModuleFinder hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
        hudsonRootModuleFinder.documentLoader = documentLoader;

        hudsonRootModuleFinder.findArtifactId("test-project");

    }

}
