package net.awired.visuwall.server.service;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.ServiceLoader;

import org.springframework.stereotype.Service;

import net.awired.visuwall.api.plugin.Plugin;

@Service
public class PluginService {

	private ServiceLoader<Plugin> pluginLoader = ServiceLoader
			.load(Plugin.class);

	public List<Plugin> getPlugins() {
		Iterator<Plugin> pluginIt = pluginLoader.iterator();
		List<Plugin> result = new ArrayList<Plugin>();
		while (pluginIt.hasNext()) {
			Plugin plugin = pluginIt.next();
			result.add(plugin);
		}
		return result;
	}

	public void reload() {
		pluginLoader.reload();
	}

}
