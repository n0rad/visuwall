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

package fr.norad.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;
import java.io.IOException;
import java.io.InputStream;
import org.junit.Test;
import com.google.common.io.ByteStreams;

public class JenkinsVersionExtractorTest {

    @Test
    public void should_extract_version() {
        String content = "6 juin 2011 21:50:35</span><a href=\"http://jenkins-ci.org/\">Jenkins ver. 1.407</a></td></tr></table></body></html>";
        JenkinsVersionExtractor jve = new JenkinsVersionExtractor(content);
        String version = jve.version();
        assertEquals("1.407", version);
    }

    @Test
    public void should_extract_version_in_jenkins_page() throws IOException {
        Class<? extends JenkinsVersionExtractorTest> clazz = this.getClass();
        ClassLoader classLoader = clazz.getClassLoader();
        InputStream stream = classLoader.getResourceAsStream("jenkins_version_page.html");
        byte[] data = ByteStreams.toByteArray(stream);
        JenkinsVersionExtractor jve = new JenkinsVersionExtractor(new String(data));

        String version = jve.version();
        assertEquals("1.407", version);
    }

}
