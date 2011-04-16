package net.awired.visuwall.server.domain;

import javax.persistence.Embeddable;

import com.google.common.base.Objects;

@Embeddable
public class Software {

    private String className;
    private float version;
    
    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
        .add("className", className) //
        .add("version", version) //
        .toString();
    }
}
