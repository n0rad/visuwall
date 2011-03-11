package com.jsmadja.wall.projectwall.domain;

import com.jsmadja.wall.projectwall.service.BuildService;
import com.jsmadja.wall.projectwall.service.HudsonService;
import com.jsmadja.wall.projectwall.service.QualityService;
import com.jsmadja.wall.projectwall.service.SonarService;

public enum Software {
    HUDSON(HudsonService.class, true, false), SONAR(SonarService.class, false, true);

    private Class<?> serviceClass;
    private boolean buildSoftware;
    private boolean qualitySoftware;

    private Software(Class<?> serviceClass, boolean buildSoftware, boolean qualitySoftware) {
        this.serviceClass = serviceClass;
        this.buildSoftware = buildSoftware;
        this.qualitySoftware = qualitySoftware;
    }

    public BuildService getBuildService() {
        try {
            return (BuildService) serviceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    public QualityService getQualityService() {
        try {
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

}
