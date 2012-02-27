package net.awired.clients.deployit.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.clients.deployit.State;

@XmlRootElement(name = "step")
@XmlAccessorType(XmlAccessType.FIELD)
public class Step {

    @XmlElement(name = "failure-count")
    private int failureCount;

    private State state;

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

}
