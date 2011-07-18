/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package net.awired.clients.hudson.resource;

import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "mavenModuleSetBuild")
@XmlAccessorType(XmlAccessType.FIELD)
public class Build {

    private int number;

    private String url;

    @XmlElements({ @XmlElement(name = "action") })
    private List<Action> actions;

    private boolean building;

    private long duration;

    private String fullDisplayName;

    private String id;

    private boolean keepLog;

    private String result;

    private long timestamp;

    private String buildOn;

    private ChangeSet changeSet;

    @XmlElements({ @XmlElement(name = "culprit") })
    private List<Culprit> culprits;

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public List<Action> getActions() {
        return actions;
    }

    public void setActions(List<Action> actions) {
        this.actions = actions;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public long getDuration() {
        return duration;
    }

    public void setDuration(long duration) {
        this.duration = duration;
    }

    public String getFullDisplayName() {
        return fullDisplayName;
    }

    public void setFullDisplayName(String fullDisplayName) {
        this.fullDisplayName = fullDisplayName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public boolean isKeepLog() {
        return keepLog;
    }

    public void setKeepLog(boolean keepLog) {
        this.keepLog = keepLog;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public String getBuildOn() {
        return buildOn;
    }

    public void setBuildOn(String buildOn) {
        this.buildOn = buildOn;
    }

    public ChangeSet getChangeSet() {
        return changeSet;
    }

    public void setChangeSet(ChangeSet changeSet) {
        this.changeSet = changeSet;
    }

    public List<Culprit> getCulprits() {
        return culprits;
    }

    public void setCulprits(List<Culprit> culprits) {
        this.culprits = culprits;
    }

}
