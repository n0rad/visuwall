package net.awired.clients.teamcity.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "running-info")
@XmlAccessorType(XmlAccessType.FIELD)
public class RunningInfo {

    @XmlAttribute
    private int estimatedTotalSeconds;

    public void setEstimatedTotalSeconds(int estimatedTotalSeconds) {
        this.estimatedTotalSeconds = estimatedTotalSeconds;
    }

    public int getEstimatedTotalSeconds() {
        return estimatedTotalSeconds;
    }

}
