/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
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

package com.jsmadja.wall.projectwall.domain;

import java.util.Arrays;
import java.util.Date;

import com.google.common.base.Objects;

public final class HudsonJob {

    private String name;
    private boolean successful;
    private String description;
    private boolean building;
    private String[] commiters;
    private int[] buildNumbers;
    private long duration;
    private Date startTime;
    private int lastBuildNumber;
    private String artifactId;
    private TestResult testResult;

    public final String getName() {
        return name;
    }

    public final void setName(String name) {
        this.name = name;
    }

    public final boolean isSuccessful() {
        return successful;
    }

    public final void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public final String getDescription() {
        return description;
    }

    public final void setDescription(String description) {
        this.description = description;
    }

    public final boolean isBuilding() {
        return building;
    }

    public final void setBuilding(boolean building) {
        this.building = building;
    }

    public final String[] getCommiters() {
        return commiters;
    }

    public final void setCommiters(String[] commiters) {
        this.commiters = commiters;
    }

    public final long getDuration() {
        return duration;
    }

    public final void setDuration(long duration) {
        this.duration = duration;
    }

    public final int[] getBuildNumbers() {
        return buildNumbers;
    }

    public final void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    public final Date getStartTime() {
        return startTime;
    }

    public final void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public final int getLastBuildNumber() {
        return lastBuildNumber;
    }

    public final void setLastBuildNumber(int lastBuildNumber) {
        this.lastBuildNumber = lastBuildNumber;
    }

    public final String getArtifactId() {
        return artifactId;
    }

    public final void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public final TestResult getTestResult() {
        return testResult;
    }

    public final void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    @Override
    public final String toString() {
        return Objects.toStringHelper(this)
        .add("name", name)
        .add("description", description)
        .add("status", successful)
        .add("isBuilding", isBuilding())
        .add("commiters", Arrays.toString(commiters))
        .add("duration", duration)
        .add("buildNumbers", Arrays.toString(buildNumbers))
        .add("startTime", startTime)
        .add("lastBuildNumber", lastBuildNumber)
        .add("artifactId", artifactId)
        .add("test result", testResult.toString())
        .toString();
    }

}
