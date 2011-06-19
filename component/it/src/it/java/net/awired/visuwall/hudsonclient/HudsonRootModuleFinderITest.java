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

package net.awired.visuwall.hudsonclient;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.exception.ArtifactIdNotFoundException;
import net.awired.visuwall.hudsonclient.finder.HudsonRootModuleFinder;
import org.junit.Assert;
import org.junit.Test;

public class HudsonRootModuleFinderITest {

    @Test
    public void should_find_synthesis_root_module_from_hudson() throws ArtifactIdNotFoundException {
        HudsonUrlBuilder hudsonUrlBuilder = new HudsonUrlBuilder(IntegrationTestData.HUDSON_URL);
        HudsonRootModuleFinder hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
        String artifactId = hudsonRootModuleFinder.findArtifactId("struts");
        Assert.assertEquals("org.apache.struts:struts-parent", artifactId);
    }

    @Test
    public void should_find_synthesis_root_module_from_jenkins() throws ArtifactIdNotFoundException {
        HudsonUrlBuilder hudsonUrlBuilder = new HudsonUrlBuilder(IntegrationTestData.JENKINS_URL);
        HudsonRootModuleFinder hudsonRootModuleFinder = new HudsonRootModuleFinder(hudsonUrlBuilder);
        String artifactId = hudsonRootModuleFinder.findArtifactId("struts 2 instable");
        Assert.assertEquals("org.apache.struts:struts2-parent", artifactId);
    }
}
