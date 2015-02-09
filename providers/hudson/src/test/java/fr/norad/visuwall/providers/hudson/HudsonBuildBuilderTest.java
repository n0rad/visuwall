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
package fr.norad.visuwall.providers.hudson;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import fr.norad.visuwall.providers.hudson.domain.HudsonBuild;
import fr.norad.visuwall.providers.hudson.domain.HudsonCommiter;
import fr.norad.visuwall.providers.hudson.resource.MavenModuleSetBuild;
import fr.norad.visuwall.providers.hudson.resource.Culprit;
import org.junit.Test;

public class HudsonBuildBuilderTest {

    @Test
    public void should_create_valid_hudson_build() {
        long duration = 123L;
        int buildNumber = 34;
        Set<HudsonCommiter> commiters = new TreeSet<HudsonCommiter>();
        commiters.add(new HudsonCommiter("dude"));
        commiters.add(new HudsonCommiter("sweet"));

        Date startTime = new Date();
        String state = null;

        List<Culprit> users = new ArrayList<Culprit>();
        users.add(new Culprit());
        users.add(new Culprit());
        users.get(0).setFullName("dude");
        users.get(1).setFullName("sweet");

        MavenModuleSetBuild setBuild = mock(MavenModuleSetBuild.class);
        when(setBuild.getDuration()).thenReturn(duration);
        when(setBuild.getCulprits()).thenReturn(users);
        when(setBuild.getNumber()).thenReturn(buildNumber);
        when(setBuild.getTimestamp()).thenReturn(startTime.getTime());

        HudsonBuildBuilder hudsonBuildBuilder = new HudsonBuildBuilder();
        HudsonBuild hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, commiters);

        assertEquals(duration, hudsonBuild.getDuration());
        assertEquals(buildNumber, hudsonBuild.getBuildNumber());
        assertEquals(commiters, hudsonBuild.getCommiters());
        assertEquals(startTime, hudsonBuild.getStartTime());
        assertEquals(state, hudsonBuild.getState());
    }

}
