package net.awired.visuwall.core.business.service;

import java.net.URL;
import java.util.List;
import java.util.Map;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;

public interface PluginServiceInterface {

    BasicCapability getPluginConnectionFromUrl(URL url, Map<String, String> properties);

    SoftwareInfo getSoftwareInfoFromUrl(URL url, Map<String, String> properties);

    PluginInfo getPluginInfo(VisuwallPlugin<BasicCapability> visuwallPlugin);

    List<PluginInfo> getPluginsInfo();

    List<VisuwallPlugin<BasicCapability>> getPlugins();

}
