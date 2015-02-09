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

package fr.norad.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import fr.norad.visuwall.api.domain.BuildState;
import org.junit.Test;

public class StatesTest {

    @Test
    public void should_map_states_correctly() {
        assertEquals(BuildState.FAILURE, States.asVisuwallState("ERROR"));
        assertEquals(BuildState.SUCCESS, States.asVisuwallState("SUCCESS"));
        assertEquals(BuildState.UNSTABLE, States.asVisuwallState("FAILURE"));
    }

    @Test
    public void should_return_unknown_when_state_is_not_mappable() {
        assertEquals(BuildState.UNKNOWN, States.asVisuwallState("not_a_state"));
    }
}
