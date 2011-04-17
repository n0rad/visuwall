package net.awired.visuwall.server.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Properties;
import java.util.ServiceLoader;

import net.awired.visuwall.api.plugin.BuildConnectionPlugin;
import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.QualityConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.server.domain.PluginHolder;
import net.awired.visuwall.server.domain.Software;
import net.awired.visuwall.server.domain.SoftwareAccess;

import org.springframework.stereotype.Service;


@Service
public class PluginService {

	
	public PluginService() {
	}
	
	private ServiceLoader<VisuwallPlugin> pluginLoader = ServiceLoader
			.load(VisuwallPlugin.class);

	public List<VisuwallPlugin> getPlugins() {
		Iterator<VisuwallPlugin> pluginIt = pluginLoader.iterator();
		List<VisuwallPlugin> result = new ArrayList<VisuwallPlugin>();
		while (pluginIt.hasNext()) {
			VisuwallPlugin plugin = pluginIt.next();
			result.add(plugin);
		}
		return result;
	}

	public void reload() {
		pluginLoader.reload();
	}
	
	public PluginHolder getPluginHolderFromSoftwares(List<SoftwareAccess> softwareAccesses) {
		PluginHolder pluginHolder = new PluginHolder();
		for (SoftwareAccess softwareAccess : softwareAccesses) {
			//TODO refactor as we recreate a connection
			VisuwallPlugin plugin = getPluginFromSoftware(softwareAccess.getSoftware());

			// connect
			Properties properties = new Properties();
//			properties.put("login", softwareAccess.getLogin());
//			properties.put("password", softwareAccess.getPassword());
			ConnectionPlugin connection = plugin.connect(softwareAccess.getUrl(), properties);

			if (connection instanceof BuildConnectionPlugin) {
				pluginHolder.addBuildService((BuildConnectionPlugin) connection);
			} else if (connection instanceof QualityConnectionPlugin) {
				pluginHolder.addQualityService((QualityConnectionPlugin) connection);
			} else {
				throw new RuntimeException("Unknow plugin connection type : " + connection);
			}
		}
		return pluginHolder;
	}
	
	public VisuwallPlugin getPluginFromSoftware(Software software) {
		List<VisuwallPlugin> plugins = getPlugins();
		for (VisuwallPlugin plugin : plugins) {
			//TODO manage version
			if (software.getClassName().equals(plugin.getClass().getName())) {
				return plugin;
			}
		}
		throw new RuntimeException("plugin not found");
	}

}
