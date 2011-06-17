package net.awired.visuwall.plugin.bamboo;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.State;

public class States {

	private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();
	static {
		STATE_MAPPING.put("Successful", State.SUCCESS);
		STATE_MAPPING.put("Failed", State.FAILURE);
	}

	public static final State asVisuwallState(String bambooState) {
		State state = STATE_MAPPING.get(bambooState);
		if (state == null) {
			state = State.UNKNOWN;
		}
		return state;
	}
}
