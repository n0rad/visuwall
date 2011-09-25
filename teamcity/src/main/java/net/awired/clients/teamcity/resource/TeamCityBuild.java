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

package net.awired.clients.teamcity.resource;

import java.util.List;
import javax.xml.bind.annotation.XmlAttribute;
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

    private TeamCityTags tags;

    private TeamCityProperties properties;

    private TeamCityRevisions revisions;

    private TeamCityVcsRoot vcsRoot;

    private TeamCityChanges changes;

    private TeamCityRelatedIssues relatedIssues;

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

    public List<TeamCityTag> getTags() {
        return tags.getTags();
    }

    public void setTags(TeamCityTags tags) {
        this.tags = tags;
    }

    public List<TeamCityProperty> getProperties() {
        return properties.getProperties();
    }

    public void setProperties(TeamCityProperties properties) {
        this.properties = properties;
    }

    public List<TeamCityRevision> getRevisions() {
        return revisions.getRevisions();
    }

    public void setRevision(TeamCityRevisions revisions) {
        this.revisions = revisions;
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

    public List<TeamCityRelatedIssue> getRelatedIssues() {
        return relatedIssues.getRelatedIssues();
    }

    public void setRelatedIssues(TeamCityRelatedIssues relatedIssues) {
        this.relatedIssues = relatedIssues;
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

}
