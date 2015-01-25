/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.providers.jenkins;

import static org.junit.Assert.assertFalse;
import java.util.List;
import org.junit.Test;
import fr.norad.visuwall.providers.hudson.domain.HudsonJob;
import fr.norad.visuwall.providers.hudson.exception.HudsonJobNotFoundException;

public class JenkinsIT {

    @Test
    public void test() throws HudsonJobNotFoundException {
        Jenkins jenkins = new Jenkins("https://builds.apache.org/", "", "");
        List<HudsonJob> projects = jenkins.findAllProjects();
        assertFalse(projects.isEmpty());
    }

}
