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

package fr.norad.visuwall.api.domain;

import com.google.common.base.Objects;

public class Commiter implements Comparable<Commiter> {

    private String id;
    private String name;
    private String email;

    public Commiter(String id) {
        this.id = id;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("id", id) //
                .add("name", name) //
                .add("email", email) //
                .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, email);
    }

    @Override
    public boolean equals(Object o) {
        if (o instanceof Commiter) {
            return Objects.equal(id, ((Commiter) o).id);
        }
        return false;
    }

    @Override
    public int compareTo(Commiter commiter) {
        if (name == null || commiter == null) {
            return 0;
        }
        return name.compareTo(commiter.name);
    }
}
