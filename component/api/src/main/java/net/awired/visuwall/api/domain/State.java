package net.awired.visuwall.api.domain;

import java.util.Arrays;

public enum State {
	SUCCESS, NEW, ABORTED, FAILURE, UNSTABLE, NOT_BUILT, UNKNOWN;

    private static final String STATE_NAMES = Arrays.toString(State.values());

    private static boolean contains(String state) {
        if (state == null) {
            return false;
        }
        return STATE_NAMES.contains(state);
    }

    public static State getStateByName(String state) {
        if (state != null) {
            state = state.toUpperCase();
            if (State.contains(state)) {
                return State.valueOf(state);
            }
        }
        return State.UNKNOWN;
    }
}