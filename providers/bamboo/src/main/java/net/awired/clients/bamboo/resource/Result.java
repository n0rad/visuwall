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

package fr.norad.visuwall.providers.bamboo.resource;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class Result {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private int number;

    @XmlAttribute
    private String lifeCycleState;

    @XmlAttribute
    private String state;

    @XmlAttribute
    private String key;

    private Link link;

    private Date buildStartedTime;

    private Date buildCompletedTime;

    private int buildDurationInSeconds;

    private int buildDuration;

    private String buildDurationDescription;

    private String buildRelativeTime;

    private int vcsRevisionKey;

    private String buildTestSummary;

    private int successfulTestCount;

    private int failedTestCount;

    private String buildReason;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getLifeCycleState() {
        return lifeCycleState;
    }

    public void setLifeCycleState(String lifeCycleState) {
        this.lifeCycleState = lifeCycleState;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    public Date getBuildStartedTime() {
        return buildStartedTime;
    }

    public void setBuildStartedTime(Date buildStartedTime) {
        this.buildStartedTime = buildStartedTime;
    }

    public Date getBuildCompletedTime() {
        return buildCompletedTime;
    }

    public void setBuildCompletedTime(Date buildCompletedTime) {
        this.buildCompletedTime = buildCompletedTime;
    }

    public int getBuildDurationInSeconds() {
        return buildDurationInSeconds;
    }

    public void setBuildDurationInSeconds(int buildDurationInSeconds) {
        this.buildDurationInSeconds = buildDurationInSeconds;
    }

    public int getBuildDuration() {
        return buildDuration;
    }

    public void setBuildDuration(int buildDuration) {
        this.buildDuration = buildDuration;
    }

    public String getBuildDurationDescription() {
        return buildDurationDescription;
    }

    public void setBuildDurationDescription(String buildDurationDescription) {
        this.buildDurationDescription = buildDurationDescription;
    }

    public String getBuildRelativeTime() {
        return buildRelativeTime;
    }

    public void setBuildRelativeTime(String buildRelativeTime) {
        this.buildRelativeTime = buildRelativeTime;
    }

    public int getVcsRevisionKey() {
        return vcsRevisionKey;
    }

    public void setVcsRevisionKey(int vcsRevisionKey) {
        this.vcsRevisionKey = vcsRevisionKey;
    }

    public String getBuildTestSummary() {
        return buildTestSummary;
    }

    public void setBuildTestSummary(String buildTestSummary) {
        this.buildTestSummary = buildTestSummary;
    }

    public int getSuccessfulTestCount() {
        return successfulTestCount;
    }

    public void setSuccessfulTestCount(int successfulTestCount) {
        this.successfulTestCount = successfulTestCount;
    }

    public int getFailedTestCount() {
        return failedTestCount;
    }

    public void setFailedTestCount(int failedTestCount) {
        this.failedTestCount = failedTestCount;
    }

    public String getBuildReason() {
        return buildReason;
    }

    public void setBuildReason(String buildReason) {
        this.buildReason = buildReason;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("id", id) //
                .add("number", number) //
                .add("lifeCycleState", lifeCycleState) //
                .add("state", state) //
                .add("key", key) //
                .add("link", link) //
                .add("build started time", buildStartedTime) //
                .add("buid completed time", buildCompletedTime) //
                .add("build duration in seconds", buildDurationInSeconds) //
                .add("build duration", buildDuration) //
                .add("build duration description", buildDurationDescription) //
                .add("build relative time", buildRelativeTime) //
                .add("vcsRevisionKey", vcsRevisionKey) //
                .add("build test summary", buildTestSummary) //
                .add("sucessful test count", successfulTestCount) //
                .add("failed test count", failedTestCount) //
                .add("build reason", buildReason) //
                .toString();
    }
}
