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

package net.awired.visuwall.plugin.teamcity;

import java.util.HashMap;
import java.util.Map;
import net.awired.visuwall.api.domain.BuildState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<String, BuildState> STATE_MAPPING = new HashMap<String, BuildState>();

    private static final String ERROR = "ERROR";
    private static final String FAILURE = "FAILURE";
    private static final String SUCCESS = "SUCCESS";

    static {
        STATE_MAPPING.put(ERROR, BuildState.FAILURE);
        STATE_MAPPING.put(FAILURE, BuildState.UNSTABLE);
        STATE_MAPPING.put(SUCCESS, BuildState.SUCCESS);
    }

    static final BuildState asVisuwallState(String teamcityState) {
        BuildState state = STATE_MAPPING.get(teamcityState);
        if (state == null) {
            state = BuildState.UNKNOWN;
            LOG.warn(teamcityState + " is not available in TeamCity plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }
}
