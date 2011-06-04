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

import java.util.Collections;
import java.util.Set;
import java.util.TreeSet;

import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;

public class Commiters {

    private Set<Commiter> commiters = new TreeSet<Commiter>();

    public void addCommiter(Commiter commiter) {
        commiters.add(commiter);
    }

    @Override
    public String toString() {
        ToStringHelper stringHelper = Objects.toStringHelper(this);
        for (Commiter commiter : commiters) {
            stringHelper.addValue(commiter);
        }
        return stringHelper.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof Commiters))
            return false;
        Commiters commiters2 = (Commiters) o;
        if (commiters.size() != commiters2.commiters.size()) {
            return false;
        }
        for (Commiter commiter : commiters) {
            if (!commiters2.contains(commiter)) {
                return false;
            }
        }
        return true;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(commiters);
    }

    public boolean contains(Commiter commiter) {
        return commiters.contains(commiter);
    }

    public Set<Commiter> asSet() {
        return Collections.unmodifiableSet(commiters);
    }
}
