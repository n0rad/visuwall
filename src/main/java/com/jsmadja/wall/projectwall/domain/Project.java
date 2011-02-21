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

public final class Project {

    private String name;
    private String description;
    private double coverage;
    private double rulesCompliance;
    private HudsonJob hudsonJob;

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

    public HudsonJob getHudsonJob() {
        return hudsonJob;
    }

    public void setHudsonJob(HudsonJob hudsonJob) {
        this.hudsonJob = hudsonJob;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name).add("description", description).add("coverage", coverage)
        .add("rules compliance", rulesCompliance).toString();
    }

}
