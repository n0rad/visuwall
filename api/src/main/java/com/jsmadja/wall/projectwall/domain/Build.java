package com.jsmadja.wall.projectwall.domain;

import java.util.Arrays;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class Build {

    private State state;
    private String[] commiters;
    private long duration;
    private Date startTime;
    private TestResult testResult;
    private int buildNumber;

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
        .add("commiters", Arrays.toString(commiters)) //
        .add("duration", duration) //
        .add("startTime", startTime);
        if (testResult != null) {
            toString.add("test result", testResult.toString());
        }
        return toString.toString();
    }
}
