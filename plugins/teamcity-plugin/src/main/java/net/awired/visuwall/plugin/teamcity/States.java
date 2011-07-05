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
import net.awired.visuwall.api.domain.State;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

    public static final String SUCCESS = "SUCCESS";
    public static final String ERROR = "ERROR";
    public static final String FAILURE = "FAILURE";

    static {
        STATE_MAPPING.put(ERROR, State.FAILURE);
        STATE_MAPPING.put(FAILURE, State.UNSTABLE);
        STATE_MAPPING.put(SUCCESS, State.SUCCESS);
    }

    public static final State asVisuwallState(String teamcityState) {
        Preconditions.checkNotNull(teamcityState, "teamcityState is mandatory");
        State state = STATE_MAPPING.get(teamcityState);
        if (state == null) {
            state = State.UNKNOWN;
            LOG.warn(teamcityState + " is not available in TeamCity plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }
}
