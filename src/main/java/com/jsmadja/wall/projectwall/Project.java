package com.jsmadja.wall.projectwall;

import com.google.common.base.Objects;

public class Project {

    private String name;
    private String description;
    private double coverage;
    private double rulesCompliance;

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

    @Override
    public String toString() {
        return Objects.toStringHelper(this)
        .add("name", name)
        .add("description", description)
        .add("coverage", coverage)
        .add("rules compliance", rulesCompliance)
        .toString();
    }

}
