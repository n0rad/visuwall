/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package fr.norad.visuwall.providers.hudson.domain;

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public final class HudsonBuild {

    private boolean successful;
    private Set<HudsonCommiter> commiters = new TreeSet<HudsonCommiter>();
    private long duration;
    private Date startTime;
    private HudsonTestResult unitTestResult;
    private HudsonTestResult integrationTestResult;
    private String state;
    private int buildNumber;

    public boolean isSuccessful() {
        return successful;
    }

    public void setSuccessful(boolean successful) {
        this.successful = successful;
    }

    public Set<HudsonCommiter> getCommiters() {
        return commiters;
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

    public HudsonTestResult getUnitTestResult() {
        return unitTestResult;
    }

    public void setUnitTestResult(HudsonTestResult testResult) {
        this.unitTestResult = testResult;
    }

    public HudsonTestResult getIntegrationTestResult() {
        return integrationTestResult;
    }

    public void setIntegrationTestResult(HudsonTestResult testResult) {
        this.integrationTestResult = testResult;
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
                .add("commiters", commiters) //
                .add("duration", duration) //
                .add("startTime", startTime) //
                .add("state", state); //
        if (unitTestResult != null) {
            toString.add("unit test result", unitTestResult.toString());
        }
        if (integrationTestResult != null) {
            toString.add("integration test result", integrationTestResult.toString());
        }
        return toString.toString();
    }

    public void setCommiters(Set<HudsonCommiter> commiters) {
        if (commiters != null) {
            this.commiters.addAll(commiters);
        }
    }
}
