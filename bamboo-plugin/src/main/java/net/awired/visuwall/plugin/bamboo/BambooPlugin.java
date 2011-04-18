package net.awired.visuwall.plugin.bamboo;

import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class BambooPlugin implements VisuwallPlugin {

	@Override
	public ConnectionPlugin connect(String url, Properties info) {
		return new BambooConnectionPlugin(url, null, null);
	}

	@Override
	public String getName() {
		return "Bamboo";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean isManageable(URL url) {
		return true;
	}

}
