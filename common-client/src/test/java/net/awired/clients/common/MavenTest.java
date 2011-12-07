package net.awired.clients.common;

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
        assertEquals("net.awired.visuwall:visuwall-hudsonclient", artifactId);
    }

    @Test(expected = MavenIdNotFoundException.class)
    public void should_not_find_artifact_id() throws Exception {
        when(documentLoader.loadFromUrl(anyString())).thenThrow(new DocumentNotLoadedException("file not found", null));

        maven.findMavenIdFrom("test-project");

    }
}
