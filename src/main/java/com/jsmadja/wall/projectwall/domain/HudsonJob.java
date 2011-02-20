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

public class HudsonJob {

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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
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

    public String[] getCommiters() {
        return commiters;
    }

    public void setCommiters(String[] commiters) {
        this.commiters = commiters;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public int[] getBuildNumbers() {
        return buildNumbers;
    }

    public void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
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

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    @Override
    public String toString() {
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
