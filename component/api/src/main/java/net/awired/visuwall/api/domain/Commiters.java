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
        if (o == null || !(o instanceof Commiters))
            return false;
        Commiters commiters2 = (Commiters) o;
        if (commiters.size() != commiters2.commiters.size())
            return false;
        for (Commiter commiter : commiters)
            if (!commiters2.contains(commiter))
                return false;
        return true;
    }

    public boolean contains(Commiter commiter) {
        return commiters.contains(commiter);
    }

    public Set<Commiter> asSet() {
        return Collections.unmodifiableSet(commiters);
    }
}
