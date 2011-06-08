package net.awired.visuwall.core.domain;

import java.util.List;

import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.domain.SoftwareId;

public class SoftwareInfo {
	private SoftwareId softwareId;
	private PluginInfo pluginInfo;
	
	//TODO move to manage also quality
	private List<String> projectNames;
	private List<String> viewNames;

	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}

	public void setPluginInfo(PluginInfo pluginInfo) {
		this.pluginInfo = pluginInfo;
	}

	public List<String> getProjectNames() {
		return projectNames;
	}

	public void setProjectNames(List<String> projectNames) {
		this.projectNames = projectNames;
	}

	public List<String> getViewNames() {
		return viewNames;
	}

	public void setViewNames(List<String> viewNames) {
		this.viewNames = viewNames;
	}

	public SoftwareId getSoftwareId() {
		return softwareId;
	}

	public void setSoftwareId(SoftwareId softwareId) {
		this.softwareId = softwareId;
	}

}
