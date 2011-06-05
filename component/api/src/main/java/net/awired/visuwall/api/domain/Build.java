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

import java.util.Date;
import java.util.Set;
import java.util.TreeSet;

import net.awired.visuwall.api.domain.ProjectStatus.State;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public final class Build {

    private State state = State.UNKNOWN;
    private Set<Commiter> commiters = new TreeSet<Commiter>();
    private long duration;
    private Date startTime;
    private TestResult unitTestResult = new TestResult();
    private TestResult integrationTestResult = new TestResult();
    private int buildNumber;
    
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

    public TestResult getUnitTestResult() {
        return unitTestResult;
    }

    public TestResult getIntegrationTestResult() {
        return integrationTestResult;
    }

    public State getState() {
        return state;
    }

    public void setState(State state) {
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
                .add("builder number", buildNumber) //
                .add("state", state) //
                .add("commiters", commiters) //
                .add("duration", duration) //
                .add("startTime", startTime);
        if (unitTestResult != null) {
            toString.add("unit test result", unitTestResult.toString());
        }
        if (integrationTestResult != null) {
            toString.add("integration test result", integrationTestResult.toString());
        }
        return toString.toString();
    }

    public void setIntegrationTestResult(TestResult integrationTestResult) {
        setTestResult(integrationTestResult, this.integrationTestResult);
    }

    public void setUnitTestResult(TestResult unitTestResult) {
        setTestResult(unitTestResult, this.unitTestResult);
    }

    private void setTestResult(TestResult from, TestResult to) {
        to.setCoverage(from.getCoverage());
        to.setFailCount(from.getFailCount());
        to.setPassCount(from.getPassCount());
        to.setSkipCount(from.getSkipCount());
    }

	public Set<Commiter> getCommiters() {
		return commiters;
	}

	public void setCommiters(Set<Commiter> commiters) {
		this.commiters = commiters;
	}
}
