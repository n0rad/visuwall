package net.awired.visuwall.core.business.service;

import java.net.URL;
import java.util.List;

import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.core.business.domain.PluginInfo;
import net.awired.visuwall.core.business.domain.SoftwareInfo;

public interface PluginServiceInterface {

	public abstract VisuwallPlugin<BasicCapability> getPluginFromUrl(URL url);

	public abstract SoftwareInfo getSoftwareInfoFromUrl(URL url);

	public abstract PluginInfo getPluginInfo(
			VisuwallPlugin<BasicCapability> visuwallPlugin);

	public abstract List<PluginInfo> getPluginsInfo();

	public abstract List<VisuwallPlugin<BasicCapability>> getPlugins();

	public abstract void reload();

}