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
