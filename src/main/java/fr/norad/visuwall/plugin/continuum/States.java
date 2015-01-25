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
package fr.norad.visuwall.plugin.continuum;

import static fr.norad.visuwall.domain.BuildState.FAILURE;
import static fr.norad.visuwall.domain.BuildState.SUCCESS;
import static fr.norad.visuwall.domain.BuildState.UNKNOWN;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import fr.norad.visuwall.domain.BuildState;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<Integer, BuildState> STATE_MAPPING = new HashMap<Integer, BuildState>();

    static {
        STATE_MAPPING.put(2, SUCCESS);
        STATE_MAPPING.put(3, FAILURE);
    }

    public static final BuildState asVisuwallState(int continuumState) {
        BuildState state = STATE_MAPPING.get(continuumState);
        if (state == null) {
            state = UNKNOWN;
            LOG.warn(continuumState + " is not available in Continuum plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }

}
