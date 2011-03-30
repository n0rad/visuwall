package net.awired.visuwall.api.domain.quality;

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
    public boolean equals(Object o) {
        if (o == null || !(o instanceof QualityMeasure)) {
            return false;
        }
        QualityMeasure qm = (QualityMeasure) o;
        boolean isEqual = true;
        if (name != null) {
            isEqual &= name.equals(qm.name);
        }
        if (value != null) {
            isEqual &= value.equals(qm.value);
        }
        if (formattedValue != null) {
            isEqual &= formattedValue.equals(qm.formattedValue);
        }
        return isEqual;
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
