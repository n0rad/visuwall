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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import java.util.ArrayList;
import java.util.List;
import net.awired.clients.hudson.resource.Build;
import net.awired.clients.hudson.resource.Culprit;
import org.junit.Test;

public class HudsonXmlHelperTest {

    @Test
    public void testIsSuccessful() {
        Build job = mock(Build.class);
        when(job.getResult()).thenReturn("SUCCESS");

        boolean isSuccessful = HudsonXmlHelper.isSuccessful(job);

        assertTrue(isSuccessful);
    }

    @Test
    public void testGetCommiters() {
        List<Culprit> users = new ArrayList<Culprit>();
        Culprit user1 = new Culprit();
        Culprit user2 = new Culprit();

        user1.setFullName("dude");
        user2.setFullName("sweet");

        users.add(user1);
        users.add(user2);

        Build setBuild = mock(Build.class);
        when(setBuild.getCulprits()).thenReturn(users);

        String[] commiters = HudsonXmlHelper.getCommiterNames(setBuild);
        assertEquals("dude", commiters[0]);
        assertEquals("sweet", commiters[1]);
    }

}
