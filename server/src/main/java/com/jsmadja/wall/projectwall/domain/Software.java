package com.jsmadja.wall.projectwall.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;

import com.google.common.base.Objects;
import com.jsmadja.wall.projectwall.service.BuildService;
import com.jsmadja.wall.projectwall.service.HudsonService;
import com.jsmadja.wall.projectwall.service.QualityService;
import com.jsmadja.wall.projectwall.service.Service;
import com.jsmadja.wall.projectwall.service.SonarService;

@Entity
@NamedQueries({
    @NamedQuery(name="findAll", query="SELECT software FROM Software software")
})
public class Software {

    @Id
    private String name;

    private String className;
    private boolean buildSoftware;
    private boolean qualitySoftware;

    public static final Software HUDSON = new Software("hudson", HudsonService.class, true, false);
    public static final Software SONAR = new Software("sonar", SonarService.class, false, true);

    public Software() { }

    public Software(String name, Class<? extends Service> clazz, boolean buildSoftware, boolean qualitySoftware) {
        this.name = name;
        this.className = clazz.getName();
        this.buildSoftware = buildSoftware;
        this.qualitySoftware = qualitySoftware;
    }

    public BuildService getBuildService() {
        try {
            Class<?> serviceClass = Class.forName(className);
            return (BuildService) serviceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public QualityService getQualityService() {
        try {
            Class<?> serviceClass = Class.forName(className);
            return (QualityService) serviceClass.newInstance();
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
    public String toString() {
        return Objects.toStringHelper(this)
        .add("name", name) //
        .add("className", className) //
        .add("buildSoftware", buildSoftware) //
        .add("qualitySoftware", qualitySoftware) //
        .toString();
    }
}
