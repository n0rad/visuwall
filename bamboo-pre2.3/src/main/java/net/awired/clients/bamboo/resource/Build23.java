package net.awired.clients.bamboo.resource;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

@XmlRootElement(name = "build")
@XmlAccessorType(XmlAccessType.FIELD)
public class Build23 {

    private String name;

    private String key;

    private Integer buildNumber;

    private String buildState;

    @XmlJavaTypeAdapter(DateAdapter.class)
    private Date buildTime;

    private Integer buildDurationInSeconds;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setBuildNumber(Integer buildNumber) {
        this.buildNumber = buildNumber;
    }

    public Integer getBuildNumber() {
        return buildNumber;
    }

    public void setBuildState(String buildState) {
        this.buildState = buildState;
    }

    public String getBuildState() {
        return buildState;
    }

    public void setBuildTime(Date buildTime) {
        this.buildTime = buildTime;
    }

    public Date getBuildTime() {
        return buildTime;
    }

    public void setBuildDurationInSeconds(Integer buildDurationInSeconds) {
        this.buildDurationInSeconds = buildDurationInSeconds;
    }

    public Integer getBuildDurationInSeconds() {
        return buildDurationInSeconds;
    }

}
