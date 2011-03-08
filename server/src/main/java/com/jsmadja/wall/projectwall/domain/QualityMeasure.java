package com.jsmadja.wall.projectwall.domain;

import com.google.common.base.Objects;

public final class QualityMeasure {

    private String name;
    private Double value;
    private String formattedValue;

    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }

    public String getFormattedValue() {
        return formattedValue;
    }
    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("name", name) //
        .add("value", value) //
        .add("formattedValue", formattedValue) //
        .toString();
    }
}
