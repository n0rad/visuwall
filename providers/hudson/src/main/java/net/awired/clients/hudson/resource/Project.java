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

package fr.norad.visuwall.providers.hudson.resource;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

@XmlAccessorType(XmlAccessType.FIELD)
public class Project {

    protected String description;

    protected String displayName;

    protected String name;

    protected String url;

    protected boolean buildable;

    @XmlElements({ @XmlElement(name = "build") })
    protected List<Build> builds = new ArrayList<Build>();

    protected String color;

    protected Build firstBuild;

    protected HealthReport healthReport;

    protected boolean inQueue;

    protected boolean keepDependencies;

    protected Build lastBuild;

    protected Build lastCompletedBuild;

    protected Build lastStableBuild;

    protected Build lastSuccessfulBuild;

    protected int nextBuildNumber;

    protected boolean concurrentBuild;

    protected Scm scm;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(String displayName) {
        this.displayName = displayName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public boolean isBuildable() {
        return buildable;
    }

    public void setBuildable(boolean buildable) {
        this.buildable = buildable;
    }

    public List<Build> getBuilds() {
        return builds;
    }

    public void setBuilds(List<Build> builds) {
        this.builds = builds;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public Build getFirstBuild() {
        return firstBuild;
    }

    public void setFirstBuild(Build firstBuild) {
        this.firstBuild = firstBuild;
    }

    public HealthReport getHealthReport() {
        return healthReport;
    }

    public void setHealthReport(HealthReport healthReport) {
        this.healthReport = healthReport;
    }

    public boolean isInQueue() {
        return inQueue;
    }

    public void setInQueue(boolean inQueue) {
        this.inQueue = inQueue;
    }

    public boolean isKeepDependencies() {
        return keepDependencies;
    }

    public void setKeepDependencies(boolean keepDependencies) {
        this.keepDependencies = keepDependencies;
    }

    public Build getLastBuild() {
        return lastBuild;
    }

    public void setLastBuild(Build lastBuild) {
        this.lastBuild = lastBuild;
    }

    public Build getLastCompletedBuild() {
        return lastCompletedBuild;
    }

    public void setLastCompletedBuild(Build lastCompletedBuild) {
        this.lastCompletedBuild = lastCompletedBuild;
    }

    public Build getLastStableBuild() {
        return lastStableBuild;
    }

    public void setLastStableBuild(Build lastStableBuild) {
        this.lastStableBuild = lastStableBuild;
    }

    public Build getLastSuccessfulBuild() {
        return lastSuccessfulBuild;
    }

    public void setLastSuccessfulBuild(Build lastSuccessfulBuild) {
        this.lastSuccessfulBuild = lastSuccessfulBuild;
    }

    public int getNextBuildNumber() {
        return nextBuildNumber;
    }

    public void setNextBuildNumber(int nextBuildNumber) {
        this.nextBuildNumber = nextBuildNumber;
    }

    public boolean isConcurrentBuild() {
        return concurrentBuild;
    }

    public void setConcurrentBuild(boolean concurrentBuild) {
        this.concurrentBuild = concurrentBuild;
    }

    public Scm getScm() {
        return scm;
    }

    public void setScm(Scm scm) {
        this.scm = scm;
    }

}
