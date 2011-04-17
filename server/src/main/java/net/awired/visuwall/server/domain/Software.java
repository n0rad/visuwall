package net.awired.visuwall.server.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.api.plugin.Plugin;
import net.awired.visuwall.api.plugin.QualityPlugin;
import net.awired.visuwall.plugin.bamboo.BambooPlugin;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;

import com.google.common.base.Objects;

@Entity
@NamedQueries({ @NamedQuery(name = "findAll", query = "SELECT software FROM Software software") })
public class Software {

    @Id
    private String name;

    private String className;
    private boolean buildSoftware;
    private boolean qualitySoftware;

    public static final Software HUDSON = new Software("hudson", HudsonPlugin.class, true, false);
    public static final Software SONAR = new Software("sonar", SonarPlugin.class, false, true);
    public static final Software BAMBOO = new Software("bamboo", BambooPlugin.class, true, false);

    public Software() {
    }

    public Software(String name, Class<? extends Plugin> clazz,
            boolean buildSoftware, boolean qualitySoftware) {
        this.name = name;
        this.className = clazz.getName();
        this.buildSoftware = buildSoftware;
        this.qualitySoftware = qualitySoftware;
    }

    public BuildPlugin getBuildService() {
        try {
            Class<?> serviceClass = Class.forName(className);
            return (BuildPlugin) serviceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public QualityPlugin getQualityService() {
        try {
            Class<?> serviceClass = Class.forName(className);
            return (QualityPlugin) serviceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isBuildSoftware() {
        return buildSoftware;
    }

    public boolean isQualitySoftware() {
        return qualitySoftware;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public void setBuildSoftware(boolean buildSoftware) {
        this.buildSoftware = buildSoftware;
    }

    public void setQualitySoftware(boolean qualitySoftware) {
        this.qualitySoftware = qualitySoftware;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || !(o instanceof Software)) {
            return false;
        }
        return name.equals(((Software) o).name);
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this).add("name", name) //
        .add("className", className) //
        .add("buildSoftware", buildSoftware) //
        .add("qualitySoftware", qualitySoftware) //
        .toString();
    }
}
