/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package net.awired.clients.hudson.helper;

import static org.junit.Assert.assertTrue;
import net.awired.clients.hudson.helper.MavenHelper;
import net.awired.clients.hudson.util.ClasspathFiles;
import org.junit.Test;

public class MavenHelperTest {

    @Test
    public void testIsMaven() {
        String projectUrl = ClasspathFiles.getAbsolutePathFile("hudson/fluxx.xml");
        boolean isMavenProject = MavenHelper.isMavenProject(projectUrl);
        assertTrue(isMavenProject);
    }

    @Test
    public void testIsNotMaven() {
        String projectUrl = ClasspathFiles.getAbsolutePathFile("simple-text-file.txt");
        boolean isNotMavenProject = MavenHelper.isNotMavenProject(projectUrl);
        assertTrue(isNotMavenProject);
    }

}
