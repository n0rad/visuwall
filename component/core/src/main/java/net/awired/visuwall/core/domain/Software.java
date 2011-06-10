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

package net.awired.visuwall.core.domain;

import javax.persistence.Embeddable;
import com.google.common.base.Objects;

@Embeddable
public class Software {

    private String className;
    private float version;

    public Software() {
    }

    public Software(String className, float version) {
        this.className = className;
        this.version = version;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("className", className) //
                .add("version", version) //
                .toString();
    }

    // ///////////////////////////////////////////////////////////

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public float getVersion() {
        return version;
    }

    public void setVersion(float version) {
        this.version = version;
    }
}
