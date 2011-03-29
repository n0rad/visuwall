package net.awired.visuwall.server.domain;

import java.util.HashSet;
import java.util.Set;

import net.awired.visuwall.api.service.BuildService;
import net.awired.visuwall.api.service.QualityService;

public class ServiceHolder {

    private Set<BuildService> buildServices = new HashSet<BuildService>();

    private Set<QualityService> qualityServices = new HashSet<QualityService>();

    public Set<BuildService> getBuildServices() {
        return buildServices;
    }

    public Set<QualityService> getQualityServices() {
        return qualityServices;
    }

}
