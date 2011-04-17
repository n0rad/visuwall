package net.awired.visuwall.plugin.sonar;

import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

public class SonarPlugin implements VisuwallPlugin {

	@Override
	public ConnectionPlugin connect(String url, Properties info) {
		SonarConnectionPlugin sonarConnectionPlugin = new SonarConnectionPlugin(
				url, null, null);
		sonarConnectionPlugin.init();
		return sonarConnectionPlugin;
	}

	@Override
	public String getName() {
		return "Sonar";
	}

	@Override
	public int getVersion() {
		return 1;
	}

	@Override
	public boolean isManagable(URL url) {
		return true;
	}

}
