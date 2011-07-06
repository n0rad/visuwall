/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.clients.bamboo.rest;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "plan")
public class Plan {

    @XmlAttribute
    private boolean enabled;

    @XmlAttribute
    private String type;

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String key;

    private String projectKey;

    private String projectName;

    private boolean isFavourite;

    private boolean isActive;

    private boolean isBuilding;

    private double averageBuildTimeInSeconds;

    private Link link;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getProjectKey() {
        return projectKey;
    }

    public void setProjectKey(String projectKey) {
        this.projectKey = projectKey;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public boolean isFavourite() {
        return isFavourite;
    }

    public void setFavourite(boolean favourite) {
        this.isFavourite = favourite;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        this.isActive = active;
    }

    public boolean isBuilding() {
        return isBuilding;
    }

    public void setBuilding(boolean building) {
        this.isBuilding = building;
    }

    public double getAverageBuildTimeInSeconds() {
        return averageBuildTimeInSeconds;
    }

    public void setAverageBuildTimeInSeconds(double averageBuildTimeInSeconds) {
        this.averageBuildTimeInSeconds = averageBuildTimeInSeconds;
    }

    public Link getLink() {
        return link;
    }

    public void setLink(Link link) {
        this.link = link;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", name) //
                .add("key", key) //
                .add("project name", projectName) //
                .add("project key", projectKey) //
                .add("link", link) //
                .toString();
    }
}
