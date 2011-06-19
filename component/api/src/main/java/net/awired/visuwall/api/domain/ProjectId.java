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

package net.awired.visuwall.api.domain;

import java.util.HashMap;
import java.util.Map;
import com.google.common.base.Objects;

public class ProjectId {

    private final Map<String, String> ids = new HashMap<String, String>();

    private String name;

    private String artifactId;

    public ProjectId() {
    }

    public ProjectId(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(artifactId, ids, name);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof ProjectId) {
            ProjectId p = (ProjectId) obj;
            return Objects.equal(getClass(), p.getClass()) && //
                    Objects.equal(artifactId, p.artifactId) && //
                    Objects.equal(ids, p.ids) && //
                    Objects.equal(name, p.name);
        }
        return false;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", name) //
                .add("artifactId", artifactId) //
                .add("ids", ids) //
                .toString();
    }

    public void addId(String idName, String idValue) {
        ids.put(idName, idValue);
    }

    public String getId(String idName) {
        return ids.get(idName);
    }

    // //////////////////////////////////////////

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }
}
