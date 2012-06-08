package net.awired.clients.pivotaltracker.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "story")
@XmlAccessorType(XmlAccessType.FIELD)
public class Story {

    @XmlElement(name = "story_type")
    private String storyType;

    @XmlElement(name = "current_state")
    private String currentState;

    public String getStoryType() {
        return storyType;
    }

    public String getCurrentState() {
        return currentState;
    }

}
