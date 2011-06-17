package net.awired.visuwall.plugin.hudson;

import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.State;

import com.google.common.base.Preconditions;

public class States {

	private static final Map<String, State> STATE_MAPPING = new HashMap<String, State>();

	static {
		STATE_MAPPING.put("success", State.SUCCESS);
		STATE_MAPPING.put("new", State.NEW);
		STATE_MAPPING.put("aborted", State.ABORTED);
		STATE_MAPPING.put("failure", State.FAILURE);
		STATE_MAPPING.put("unstable", State.UNSTABLE);
		STATE_MAPPING.put("not_build", State.NOTBUILT);
	}

	public static final State asVisuwallState(String hudsonState) {
		Preconditions.checkNotNull(hudsonState, "hudsonState is mandatory");
		hudsonState = hudsonState.toLowerCase();
		State state = STATE_MAPPING.get(hudsonState);
		if (state == null) {
			state = State.UNKNOWN;
		}
		return state;
	}
}
