package net.awired.visuwall.plugin.hudson;

import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class HudsonPlugin implements VisuwallPlugin {

	@Override
	public ConnectionPlugin connect(String url, Properties info) {
		return new HudsonConnectionPlugin(url, null, null);
	}

	@Override
	public String getName() {
		return "Hudson";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean isManageable(URL url) {
		//TODO it
		return true;
	}
}