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

package net.awired.visuwall.plugin.hudson;

import java.util.HashMap;
import java.util.Map;
import net.awired.visuwall.api.domain.BuildState;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

    private static final Map<String, BuildState> STATE_MAPPING = new HashMap<String, BuildState>();

    private States() {
    }

    static {
        STATE_MAPPING.put("success", BuildState.SUCCESS);
        STATE_MAPPING.put("aborted", BuildState.ABORTED);
        STATE_MAPPING.put("failure", BuildState.FAILURE);
        STATE_MAPPING.put("unstable", BuildState.UNSTABLE);
    }

    public static final BuildState asVisuwallState(String hudsonState) {
        Preconditions.checkNotNull(hudsonState, "hudsonState is mandatory");
        hudsonState = hudsonState.toLowerCase();
        BuildState state = STATE_MAPPING.get(hudsonState);
        if (state == null) {
            state = BuildState.UNKNOWN;
            LOG.warn(hudsonState + " is not available in Hudson plugin. Please report it to Visuwall dev team.");
        }
        return state;
    }
}
