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

package net.awired.visuwall.plugin.bamboo;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.State;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class States {

    private static final Logger LOG = LoggerFactory.getLogger(States.class);

	private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

	static {
		STATE_MAPPING.put("Successful", State.SUCCESS);
		STATE_MAPPING.put("Failed", State.FAILURE);
	}

	public static final State asVisuwallState(String bambooState) {
		State state = STATE_MAPPING.get(bambooState);
		if (state == null) {
			state = State.UNKNOWN;
            LOG.warn(bambooState + " is not available in Bamboo plugin. Please report it to Visuwall dev team.");
		}
		return state;
	}
}
