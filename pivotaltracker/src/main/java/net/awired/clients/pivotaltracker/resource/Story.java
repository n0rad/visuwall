package net.awired.clients.pivotaltracker.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "story")
@XmlAccessorType(XmlAccessType.FIELD)
public class Story {

    private Integer id;

    private String name;

    @XmlElement(name = "story_type")
    private StoryType storyType;

    @XmlElement(name = "current_state")
    private CurrentState currentState;

    public Story() {
    }

    public Story(StoryType storyType, CurrentState currentState) {
        this.storyType = storyType;
        this.currentState = currentState;
    }

    public StoryType getStoryType() {
        return storyType;
    }

    public CurrentState getCurrentState() {
        return currentState;
    }

    @Override
    public String toString() {
        String storyTypeName = storyType == null ? "" : storyType.name();
        String currentStateName = currentState == null ? "" : currentState.name();
        return id + ".[" + storyTypeName + "][" + currentStateName + "] " + name;
    }

}
