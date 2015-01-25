/**
 *
 *     Copyright (C) norad.fr
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */
package fr.norad.visuwall.providers.pivotal.resource;

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
