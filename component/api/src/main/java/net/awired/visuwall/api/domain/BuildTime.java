package net.awired.visuwall.api.domain;

import java.util.Date;

public class BuildTime {

    private Date startTime;

    private long duration;

    public void setStartTime(Date startTime) {
        this.startTime = startTime;
    }

    public Date getStartTime() {
        return startTime;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public long getDuration() {
        return duration;
    }
}
