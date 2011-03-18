package net.awired.visuwall.api.domain;

import com.google.common.base.Objects;

public final class ProjectStatus {
    private String name;
    private boolean building;
    private int lastBuildId;
    private State state;

    public enum State {
        SUCCESS, NEW, ABORTED, FAILURE, UNSTABLE
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
