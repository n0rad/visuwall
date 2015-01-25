/**
 *
 *     Copyright (C) norad.fr
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
package fr.norad.visuwall.domain.quality;

import com.google.common.base.Objects;

public final class QualityMetric {

    private String key;
    private String name;
    private String description;
    private String domain;
    private Boolean qualitative;
    private Integer direction;
    private Boolean userManaged;
    private String valTyp;
    private Boolean hidden;

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

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

    public String getDomain() {
        return domain;
    }

    public void setDomain(String domain) {
        this.domain = domain;
    }

    public Boolean getQualitative() {
        return qualitative;
    }

    public void setQualitative(Boolean qualitative) {
        this.qualitative = qualitative;
    }

    public Integer getDirection() {
        return direction;
    }

    public void setDirection(Integer direction) {
        this.direction = direction;
    }

    public Boolean getUserManaged() {
        return userManaged;
    }

    public void setUserManaged(Boolean userManaged) {
        this.userManaged = userManaged;
    }

    public String getValTyp() {
        return valTyp;
    }

    public void setValTyp(String valTyp) {
        this.valTyp = valTyp;
    }

    public Boolean getHidden() {
        return hidden;
    }

    public void setHidden(Boolean hidden) {
        this.hidden = hidden;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("description", description)//
                .add("direction", direction)//
                .add("domain", domain)//
                .add("hidden", hidden)//
                .add("key", key)//
                .add("name", name)//
                .add("qualitative", qualitative)//
                .add("userManaged", userManaged)//
                .add("valTyp", valTyp)//
                .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof QualityMetric) {
            return Objects.equal(key, ((QualityMetric) o).key);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(description, domain, key, name);
    }
}
