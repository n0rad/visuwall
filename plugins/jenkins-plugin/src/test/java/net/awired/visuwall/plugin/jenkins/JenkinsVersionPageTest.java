package net.awired.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import java.io.IOException;
import java.io.InputStream;
import net.awired.visuwall.api.domain.SoftwareId;
import org.junit.Test;
import com.google.common.io.ByteStreams;

public class JenkinsVersionPageTest {

    @Test
    public void should_load_api_page() throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream("jenkins_version_page.html");
        byte[] data = ByteStreams.toByteArray(stream);
        String content = new String(data);
        JenkinsVersionPage versionPage = new JenkinsVersionPage(content);
        boolean isVersionPage = versionPage.isJenkinsApiPage();
        assertTrue(isVersionPage);

        SoftwareId softwareId = versionPage.createSoftwareId();

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.407", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

}
