package com.jsmadja.wall.hudsonclient.domain;

import java.util.Arrays;
import java.util.Date;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class HudsonBuild {

    private boolean successful;
    private String[] commiters;
    private long duration;
    private Date startTime;
    private TestResult testResult;

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

    @Override
    public String toString() {
        ToStringHelper toString = Objects.toStringHelper(this)
        .add("status", successful)
        .add("commiters", Arrays.toString(commiters)).add("duration", duration)
        .add("startTime", startTime);
        if (testResult != null) {
            toString.add("test result", testResult.toString());
        }
        return toString.toString();
    }
}
