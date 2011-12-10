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
    public void should_load_api_page_with_warnings() throws Exception {
        JenkinsVersionPage versionPage = loadJenkinsVersionPage("jenkins_version_page_v1404.html");
        boolean isVersionPage = versionPage.isJenkinsApiPage();
        assertTrue(isVersionPage);

        SoftwareId softwareId = versionPage.createSoftwareId();

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.404", softwareId.getVersion());
        assertEquals("This jenkins version has a bug with git project. Git project wont be display.",
                softwareId.getWarnings());
    }

    @Test
    public void should_load_api_page() throws Exception {
        JenkinsVersionPage versionPage = loadJenkinsVersionPage("jenkins_version_page.html");
        boolean isVersionPage = versionPage.isJenkinsApiPage();
        assertTrue(isVersionPage);

        SoftwareId softwareId = versionPage.createSoftwareId();

        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.407", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

    @Test
    public void should_not_failed_if_version_is_not_a_number() throws Exception {
        JenkinsVersionPage versionPage = loadJenkinsVersionPage("jenkins_ci_version_page.html");
        SoftwareId softwareId = versionPage.createSoftwareId();
        assertEquals("Jenkins", softwareId.getName());
        assertEquals("1.443-SNAPSHOT (rc-12/05/2011 18:56 GMT-kohsuke)", softwareId.getVersion());
        assertNull(softwareId.getWarnings());
    }

    private JenkinsVersionPage loadJenkinsVersionPage(String pageName) throws IOException {
        InputStream stream = this.getClass().getClassLoader().getResourceAsStream(pageName);
        byte[] data = ByteStreams.toByteArray(stream);
        String content = new String(data);
        JenkinsVersionPage versionPage = new JenkinsVersionPage(content);
        return versionPage;
    }

}
