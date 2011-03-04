package com.jsmadja.wall.projectwall.domain;

import com.jsmadja.wall.projectwall.service.HudsonService;
import com.jsmadja.wall.projectwall.service.Service;
import com.jsmadja.wall.projectwall.service.SonarService;

public enum Software {
    HUDSON(HudsonService.class), SONAR(SonarService.class);

    private Class<? extends Service> serviceClass;

    private Software(Class<? extends Service> serviceClass) {
        this.serviceClass = serviceClass;
    }

    public Service getService() {
        try {
            return serviceClass.newInstance();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
