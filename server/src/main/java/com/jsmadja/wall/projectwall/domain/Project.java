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

package com.jsmadja.wall.projectwall.domain;

import com.google.common.base.Objects;
import com.jsmadja.wall.hudsonclient.domain.HudsonProject;

public final class Project {

    private String id;
    private String name;
    private String description;
    private double coverage;
    private double rulesCompliance;
    private HudsonProject hudsonProject;
    private QualityResult qualityResult;

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

    public HudsonProject getHudsonProject() {
        return hudsonProject;
    }

    public void setHudsonProject(HudsonProject hudsonProject) {
        this.hudsonProject = hudsonProject;
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

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Project)) {
            return false;
        }
        Project project = (Project)obj;
        return id.equals(project.id) && name.equals(project.name);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
        .add("id", id) //
        .add("name", name) //
        .add("description", description) //
        .add("coverage", coverage) //
        .add("rules compliance", rulesCompliance) //
        .toString();
    }

}
