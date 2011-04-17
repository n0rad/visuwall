package net.awired.visuwall.api.domain;

import java.util.Arrays;

import com.google.common.base.Objects;

public final class ProjectStatus {
    private String name;
    private boolean building;
    private int lastBuildId;
    private State state;

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public int getLastBuildId() {
        return lastBuildId;
    }

    public void setLastBuildId(int lastBuildId) {
        this.lastBuildId = lastBuildId;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("name", name) //
        .add("last build id", lastBuildId) //
        .add("state", state) //
        .add("building", building) //
        .toString();
    }
}
