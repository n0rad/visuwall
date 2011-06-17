package net.awired.visuwall.plugin.teamcity;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.State;

import com.google.common.base.Preconditions;

public class States {

	private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

	static {
	}

	public static final State asVisuwallState(String teamcityState) {
		Preconditions.checkNotNull(teamcityState, "teamcityState is mandatory");
		teamcityState = teamcityState.toLowerCase();
		State state = STATE_MAPPING.get(teamcityState);
		if (state == null) {
			state = State.UNKNOWN;
		}
		return state;
	}
}
