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
package fr.norad.visuwall.providers.teamcity.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "build")
public class TeamCityBuild extends TeamCityAbstractBuild {

    @XmlAttribute
    private boolean personal;

    @XmlAttribute
    private boolean history;

    @XmlAttribute
    private boolean pinned;

    @XmlAttribute
    private boolean running;

    @XmlAttribute
    private String number;

    private String statusText;

    private TeamCityBuildType buildType;

    private String startDate;

    private String finishDate;

    private TeamCityAgent agent;

    @XmlElement(name = "running-info")
    private RunningInfo runningInfo;

    @XmlElementWrapper(name = "tags")
    @XmlElements({ @XmlElement(name = "tag") })
    private List<TeamCityTag> tags = new ArrayList<TeamCityTag>();

    @XmlElementWrapper(name = "properties")
    @XmlElements(@XmlElement(name = "property"))
    private List<TeamCityProperty> properties = new ArrayList<TeamCityProperty>();

    @XmlElementWrapper(name = "revisions")
    @XmlElements(@XmlElement(name = "revision"))
    private List<TeamCityRevision> revisions = new ArrayList<TeamCityRevision>();

    private TeamCityVcsRoot vcsRoot;

    private TeamCityChanges changes;

    @XmlElementWrapper(name = "relatedIssues")
    @XmlElements({ @XmlElement(name = "relatedIssue") })
    private List<TeamCityRelatedIssue> relatedIssues = new ArrayList<TeamCityRelatedIssue>();

    public boolean isPersonal() {
        return personal;
    }

    public void setPersonal(boolean personal) {
        this.personal = personal;
    }

    public boolean isHistory() {
        return history;
    }

    public void setHistory(boolean history) {
        this.history = history;
    }

    public boolean isPinned() {
        return pinned;
    }

    public void setPinned(boolean pinned) {
        this.pinned = pinned;
    }

    public String getStatusText() {
        return statusText;
    }

    public void setStatusText(String statusText) {
        this.statusText = statusText;
    }

    public TeamCityBuildType getBuildType() {
        return buildType;
    }

    public void setBuildType(TeamCityBuildType buildType) {
        this.buildType = buildType;
    }

    public String getStartDate() {
        return startDate;
    }

    public String getFinishDate() {
        return finishDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public void setFinishDate(String finishDate) {
        this.finishDate = finishDate;
    }

    public TeamCityAgent getAgent() {
        return agent;
    }

    public void setAgent(TeamCityAgent agent) {
        this.agent = agent;
    }

    public TeamCityVcsRoot getVcsRoot() {
        return vcsRoot;
    }

    public void setVcsRoot(TeamCityVcsRoot vcsRoot) {
        this.vcsRoot = vcsRoot;
    }

    public TeamCityChanges getChanges() {
        return changes;
    }

    public void setChanges(TeamCityChanges changes) {
        this.changes = changes;
    }

    public String getNumber() {
        return number;
    }

    public void setNumber(String number) {
        this.number = number;
    }

    public boolean isRunning() {
        return running;
    }

    public void setRunning(boolean running) {
        this.running = running;
    }

    public List<TeamCityProperty> getProperties() {
        return properties;
    }

    public void setProperties(List<TeamCityProperty> properties) {
        this.properties = properties;
    }

    public List<TeamCityRevision> getRevisions() {
        return revisions;
    }

    public void setRevisions(List<TeamCityRevision> revisions) {
        this.revisions = revisions;
    }

    public List<TeamCityTag> getTags() {
        return tags;
    }

    public void setTags(List<TeamCityTag> tags) {
        this.tags = tags;
    }

    public List<TeamCityRelatedIssue> getRelatedIssues() {
        return relatedIssues;
    }

    public void setRelatedIssues(List<TeamCityRelatedIssue> relatedIssues) {
        this.relatedIssues = relatedIssues;
    }

    public void setRunningInfo(RunningInfo runningInfo) {
        this.runningInfo = runningInfo;
    }

    public RunningInfo getRunningInfo() {
        return runningInfo;
    }

}
