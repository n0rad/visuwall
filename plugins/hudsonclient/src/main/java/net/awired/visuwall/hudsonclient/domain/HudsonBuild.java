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

package net.awired.visuwall.hudsonclient.domain;

import java.util.Arrays;
import java.util.Date;

import net.awired.visuwall.api.domain.TestResult;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public final class HudsonBuild {

    private boolean successful;
    private String[] commiters;
    private long duration;
    private Date startTime;
    private TestResult testResult;
    private String state;
    private int buildNumber;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public String[] getCommiters() {
        return commiters;
    }

    public void setCommiters(String[] commiters) {
        this.commiters = commiters.clone();
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }


    public Date getStartTime() {
        return startTime;
    }

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public TestResult getTestResult() {
        return testResult;
    }

    public void setTestResult(TestResult testResult) {
        this.testResult = testResult;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    @Override
    public String toString() {
        ToStringHelper toString = Objects.toStringHelper(this) //
        .add("build number", buildNumber) //
        .add("status", successful) //
        .add("commiters", Arrays.toString(commiters)) //
        .add("duration", duration) //
        .add("startTime", startTime) //
        .add("state", state); //
        if (testResult != null) {
            toString.add("test result", testResult.toString());
        }
        return toString.toString();
    }
}
