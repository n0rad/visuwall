package net.awired.visuwall.core.business.service;

import java.net.URL;
import java.util.List;

import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;

public interface PluginServiceInterface {

	VisuwallPlugin<BasicCapability> getPluginFromUrl(URL url);

	SoftwareInfo getSoftwareInfoFromUrl(URL url);

	PluginInfo getPluginInfo(VisuwallPlugin<BasicCapability> visuwallPlugin);

	List<PluginInfo> getPluginsInfo();

	List<VisuwallPlugin<BasicCapability>> getPlugins();

}