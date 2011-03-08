package com.jsmadja.wall.projectwall.domain;

import com.google.common.base.Objects;

public final class QualityMeasure {

    private Double value;
    private String formattedValue;
    private QualityMetric metric;

    public Double getValue() {
        return value;
    }
    public void setValue(Double value) {
        this.value = value;
    }
    public QualityMetric getMetric() {
        return metric;
    }
    public void setMetric(QualityMetric metric) {
        this.metric = metric;
    }
    public String getFormattedValue() {
        return formattedValue;
    }
    public void setFormattedValue(String formattedValue) {
        this.formattedValue = formattedValue;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("value", value) //
        .add("formattedValue", formattedValue) //
        .add("metric", metric.toString()) //
        .toString();
    }
}
