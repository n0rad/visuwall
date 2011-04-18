package net.awired.visuwall.core.domain;

import java.util.HashSet;
import java.util.Set;

import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;

public class PluginHolder {

    private final Set<BuildConnectionPlugin> buildServices = new HashSet<BuildConnectionPlugin>();

    private final Set<QualityConnectionPlugin> qualityServices = new HashSet<QualityConnectionPlugin>();

    public void addBuildService(BuildConnectionPlugin buildPlugin) {
        buildServices.add(buildPlugin);
    }

    public void addQualityService(QualityConnectionPlugin qualityPlugin) {
        qualityServices.add(qualityPlugin);
    }

    ////////////////////////////////////////////////////

    public Set<BuildConnectionPlugin> getBuildServices() {
        return buildServices;
    }

    public Set<QualityConnectionPlugin> getQualityServices() {
        return qualityServices;
    }

}
