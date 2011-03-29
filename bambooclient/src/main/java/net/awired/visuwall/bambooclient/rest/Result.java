package net.awired.visuwall.bambooclient.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class Result {

    @XmlAttribute
    public Integer id;

    @XmlAttribute
    public Integer number;

    @XmlAttribute
    public String lifeCycleState;

    @XmlAttribute
    public String state;

    @XmlAttribute
    public String key;

    public Link link;

    public Date buildStartedTime;

    public Date buildCompletedTime;

    public Integer buildDurationInSeconds;

    public Integer buildDuration;

    public String buildDurationDescription;

    public String buildRelativeTime;

    public Integer vcsRevisionKey;

    public String buildTestSummary;

    public Integer successfulTestCount;

    public String failedTestCount;

    public String buildReason;
}
