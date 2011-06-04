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

package net.awired.visuwall.hudsonclient.builder;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.Commiters;
import net.awired.visuwall.hudsonclient.HudsonJerseyClient;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;

import org.junit.Test;

public class HudsonUserBuilderTest {

    @Test
    public void should_get_commiter() {
        HudsonUser user = new HudsonUser();
        user.setId("jsmadja");
        user.setName("Julien Smadja");
        user.setEmail("jsmadja@xebia.fr");

        HudsonJerseyClient hudsonJerseyClient = mock(HudsonJerseyClient.class);
        when(hudsonJerseyClient.getHudsonUser(anyString())).thenReturn(user);

        HudsonUrlBuilder hudsonUrlBuilder = mock(HudsonUrlBuilder.class);
        HudsonUserBuilder hudsonUserBuilder = new HudsonUserBuilder(hudsonUrlBuilder, hudsonJerseyClient);

        Commiters commiters = hudsonUserBuilder.getCommiters(new String[] { "Julien Smadja" });

        Commiter commiter = commiters.asSet().iterator().next();
        assertEquals("jsmadja", commiter.getId());
        assertEquals("Julien Smadja", commiter.getName());
        assertEquals("jsmadja@xebia.fr", commiter.getEmail());
    }
}
