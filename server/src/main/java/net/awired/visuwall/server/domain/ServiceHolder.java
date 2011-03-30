package net.awired.visuwall.server.domain;

import java.util.HashSet;
import java.util.Set;

import net.awired.visuwall.api.plugin.BuildPlugin;
import net.awired.visuwall.api.plugin.QualityPlugin;

public class ServiceHolder {

    private Set<BuildPlugin> buildServices = new HashSet<BuildPlugin>();

    private Set<QualityPlugin> qualityServices = new HashSet<QualityPlugin>();

    public Set<BuildPlugin> getBuildServices() {
        return buildServices;
    }

    public Set<QualityPlugin> getQualityServices() {
        return qualityServices;
    }

}
