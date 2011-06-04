package net.awired.visuwall.api.domain;

public class SoftwareInfo {

	private String name;
	private String version;
	private String[] warnings;
	private PluginInfo pluginInfo;

	// ////////////////////////////////
	
	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public String[] getWarnings() {
		return warnings;
	}

	public void setWarnings(String[] warnings) {
		this.warnings = warnings;
	}

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
}
