package net.awired.visuwall.api.domain;

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
        if (o != null && o instanceof Commiter) {
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
