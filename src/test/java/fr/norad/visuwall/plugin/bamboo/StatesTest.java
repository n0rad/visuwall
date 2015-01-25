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
package fr.norad.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;
import fr.norad.visuwall.domain.BuildState;

public class StatesTest {

    @Test
    public void should_convert_as_visuwall_state() {
        assertEquals(BuildState.SUCCESS, States.asVisuwallState("Successful"));
        assertEquals(BuildState.FAILURE, States.asVisuwallState("Failed"));
        assertEquals(BuildState.UNKNOWN, States.asVisuwallState("Not a valid bamboo state"));
        assertEquals(BuildState.UNKNOWN, States.asVisuwallState(null));
    }

}
