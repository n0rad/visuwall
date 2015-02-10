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

import java.util.HashMap;
import java.util.Map;
import fr.norad.visuwall.api.domain.BuildState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<String, BuildState> STATE_MAPPING = new HashMap<String, BuildState>();

    static {
        STATE_MAPPING.put("Successful", BuildState.SUCCESS);
        STATE_MAPPING.put("Failed", BuildState.FAILURE);
    }

    public static final BuildState asVisuwallState(String bambooState) {
        BuildState state = STATE_MAPPING.get(bambooState);
        if (state == null) {
            state = BuildState.UNKNOWN;
            LOG.warn(bambooState + " is not available in Bamboo plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }
}
