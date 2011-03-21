/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.api.domain;

import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.domain.quality.QualityResult;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public final class Project implements Comparable<Project> {

    private String id;
    private String name;
    private String description;
    private double coverage;
    private double rulesCompliance;
    private QualityResult qualityResult;
    private State state;
    private int[] buildNumbers;

    private Build completedBuild;
    private Build currentBuild;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getCoverage() {
        return coverage;
    }

    public void setCoverage(double coverage) {
        this.coverage = coverage;
    }

    public double getRulesCompliance() {
        return rulesCompliance;
    }

    public void setRulesCompliance(double rulesCompliance) {
        this.rulesCompliance = rulesCompliance;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public QualityResult getQualityResult() {
        return qualityResult;
    }

    public void setQualityResult(QualityResult qualityResult) {
        this.qualityResult = qualityResult;

    }
    public State getState() {
        return state;
    }

    public void setState(State state) {
        this.state = state;
    }

    public int[] getBuildNumbers() {
        return buildNumbers;
    }

    public void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof Project)) {
            return false;
        }

        if (id == null || name == null) {
            return false;
        }

        Project project = (Project)obj;
        return id.equals(project.id) && name.equals(project.name);
    }

    public Build getCompletedBuild() {
        return completedBuild;
    }

    public void setCompletedBuild(Build completedBuild) {
        this.completedBuild = completedBuild;
    }

    public Build getCurrentBuild() {
        return currentBuild;
    }

    public void setCurrentBuild(Build currentBuild) {
        this.currentBuild = currentBuild;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
        .add("id", id) //
        .add("name", name) //
        .add("description", description) //
        .add("coverage", coverage) //
        .add("rules compliance", rulesCompliance) //
        .add("state", state) //
        .add("completed build", completedBuild) //
        .toString();
    }

    @Override
    public int compareTo(Project project) {
        Preconditions.checkNotNull(project, "project");
        return name.compareTo(project.name);
    }

}
