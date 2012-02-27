package net.awired.clients.deployit.resource;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.clients.deployit.State;

@XmlRootElement(name = "task")
@XmlAccessorType(XmlAccessType.FIELD)
public class Task {

    private String application;

    @XmlElement(name = "completion-date")
    private Date completionDate;

    private String environment;

    @XmlElement(name = "failure-count")
    private int failureCount;

    private String label;

    @XmlElement(name = "start-date")
    private Date startDate;

    private State state;

    private String user;

    private Integer version;

    @XmlElementWrapper(name = "steps")
    @XmlElements({ @XmlElement(name = "step") })
    private List<Step> steps;

    public void setApplication(String application) {
        this.application = application;
    }

    public String getApplication() {
        return application;
    }

    public void setCompletionDate(Date completionDate) {
        this.completionDate = completionDate;
    }

    public Date getCompletionDate() {
        return completionDate;
    }

    public void setEnvironment(String environment) {
        this.environment = environment;
    }

    public String getEnvironment() {
        return environment;
    }

    public void setFailureCount(int failureCount) {
        this.failureCount = failureCount;
    }

    public int getFailureCount() {
        return failureCount;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return user;
    }

    public void setVersion(Integer version) {
        this.version = version;
    }

    public Integer getVersion() {
        return version;
    }

    public void setSteps(List<Step> steps) {
        this.steps = steps;
    }

    public List<Step> getSteps() {
        return steps;
    }

}
