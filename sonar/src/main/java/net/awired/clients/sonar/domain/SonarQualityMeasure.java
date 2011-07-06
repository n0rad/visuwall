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

package net.awired.clients.sonar.domain;

import com.google.common.base.Objects;

public final class SonarQualityMeasure {

    private String name;
    private Double value;
    private String formattedValue;
    private String key;

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

    public void setKey(String key) {
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof SonarQualityMeasure)) {
            return false;
        }
        SonarQualityMeasure qm = (SonarQualityMeasure) o;
        return Objects.equal(key, qm.key);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(name, key, formattedValue, value);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("key", key) //
                .add("name", name) //
                .add("value", value) //
                .add("formattedValue", formattedValue) //
                .toString();
    }
}
