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

package net.awired.visuwall.core.business.domain;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.TreeSet;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityResult;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class Build {

    private final int buildNumber;
    private boolean building;
    private State state = State.UNKNOWN;
    private Set<Commiter> commiters = new TreeSet<Commiter>();
    private long duration;
    private Date startTime;
    private Date estimatedFinishTime;
    private final Map<SoftwareProjectId, CapabilitiesResult> capabilitiesResults = new HashMap<SoftwareProjectId, CapabilitiesResult>();

    public Build(int buildNumber) {
        this.buildNumber = buildNumber;
    }

    //TODO move and redo it with dates
    public void mergeResult(TestResult dest, TestResult source) {
        if (dest.getTotalCount() == 0) {
            dest.setCoverage(source.getCoverage());
            dest.setFailCount(source.getFailCount());
            dest.setPassCount(source.getPassCount());
            dest.setSkipCount(source.getSkipCount());
        }
        if (source.getCoverage() != 0) {
            dest.setCoverage(source.getCoverage());
        }

        if (source.getTotalCount() != 0 && source.getCoverage() == 0) {
            // try to force with build stats
            dest.setFailCount(source.getFailCount());
            dest.setPassCount(source.getPassCount());
            dest.setSkipCount(source.getSkipCount());
        }
    }

    public TestResult getUnitTestResult() {
        // TODO change that 
        TestResult result = new TestResult();
        for (SoftwareProjectId softwareProjectId : capabilitiesResults.keySet()) {
            CapabilitiesResult capabilitiesResult = capabilitiesResults.get(softwareProjectId);
            if (capabilitiesResult.getUnitTestResult() != null) {
                TestResult softwareRes = capabilitiesResult.getUnitTestResult();
                mergeResult(result, softwareRes);
            }
        }
        return result;
    }

    public QualityResult getQualityResult() {
        for (SoftwareProjectId softwareProjectId : capabilitiesResults.keySet()) {
            CapabilitiesResult capabilitiesResult = capabilitiesResults.get(softwareProjectId);
            if (capabilitiesResult.getQualityResult() != null) {
                return capabilitiesResult.getQualityResult();
            }
        }
        return null;
    }

    public TestResult getIntegrationTestResult() {
        TestResult result = new TestResult();
        for (SoftwareProjectId softwareProjectId : capabilitiesResults.keySet()) {
            CapabilitiesResult capabilitiesResult = capabilitiesResults.get(softwareProjectId);
            if (capabilitiesResult.getIntegrationTestResult() != null) {
                TestResult softwareRes = capabilitiesResult.getIntegrationTestResult();
                mergeResult(result, softwareRes);
            }
        }
        return result;
    }

    ///////////////////////////////////////////////////////////////////////////////

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

    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int getBuildNumber() {
        return buildNumber;
    }

    @Override
    public String toString() {
        ToStringHelper toString = Objects.toStringHelper(this) //
                .add("builderNumber", buildNumber) //
                .add("state", state) //
                .add("commiters", commiters) //
                .add("duration", duration) //
                .add("startTime", startTime);
        return toString.toString();
    }

    public Set<Commiter> getCommiters() {
        return commiters;
    }

    public void setCommiters(Set<Commiter> commiters) {
        this.commiters = commiters;
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Build) {
            Build b = (Build) o;
            return Objects.equal(buildNumber, b.buildNumber) && //
                    Objects.equal(commiters, b.commiters) && //
                    Objects.equal(duration, b.duration) && //
                    Objects.equal(startTime, b.startTime) && //
                    Objects.equal(state, b.state); //
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(duration, buildNumber, startTime);
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public void setEstimatedFinishTime(Date estimatedFinishTime) {
        this.estimatedFinishTime = estimatedFinishTime;
    }

    public Date getEstimatedFinishTime() {
        return estimatedFinishTime;
    }

    public Map<SoftwareProjectId, CapabilitiesResult> getCapabilitiesResults() {
        return capabilitiesResults;
    }

}
