/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsmadja.wall.hudsonclient.domain;

import java.util.Arrays;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public final class HudsonProject {

    private String name;
    private String description;
    private int[] buildNumbers;
    private int lastBuildNumber = -1;
    private String artifactId;
    private boolean building;

    private HudsonBuild completedBuild;

    private HudsonBuild currentBuild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public int[] getBuildNumbers() {
        return buildNumbers;
    }

    public void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers.clone();
    }

    public int getLastBuildNumber() {
        return lastBuildNumber;
    }

    public void setLastBuildNumber(int lastBuildNumber) {
        this.lastBuildNumber = lastBuildNumber;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public HudsonBuild getCompletedBuild() {
        return completedBuild;
    }

    public void setCompletedBuild(HudsonBuild completedBuild) {
        this.completedBuild = completedBuild;
    }

    public HudsonBuild getCurrentBuild() {
        return currentBuild;
    }

    public void setCurrentBuild(HudsonBuild currentBuild) {
        this.currentBuild = currentBuild;
    }

    @Override
    public String toString() {
        ToStringHelper toString = Objects.toStringHelper(this) //
        .add("name", name) //
        .add("description", description) //
        .add("isBuilding", isBuilding()) //
        .add("buildNumbers", Arrays.toString(buildNumbers)) //
        .add("lastBuildNumber", lastBuildNumber) //
        .add("artifactId", artifactId);
        return toString.toString();
    }

}
