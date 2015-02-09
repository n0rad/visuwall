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

package fr.norad.visuwall.providers.common;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.w3c.dom.Document;

public class MavenTest {

    @InjectMocks
    Maven maven = new Maven();

    @Mock
    DocumentLoader documentLoader;

    @Before
    public void init() {
        initMocks(this);
    }

    @Test
    public void should_find_artifact_id() throws Exception {
        String pomUrl = ClasspathFiles.getAbsolutePathFile("pom-sample.xml");
        Document document = new DocumentLoader().loadFromUrl(pomUrl);
        when(documentLoader.loadFromUrl(anyString())).thenReturn(document);

        String artifactId = maven.findMavenIdFrom("test-project");
        assertEquals("fr.norad.visuwall:visuwall-hudsonclient", artifactId);
    }

    @Test(expected = MavenIdNotFoundException.class)
    public void should_not_find_artifact_id() throws Exception {
        when(documentLoader.loadFromUrl(anyString())).thenThrow(new DocumentNotLoadedException("file not found", null));

        maven.findMavenIdFrom("test-project");

    }
}
