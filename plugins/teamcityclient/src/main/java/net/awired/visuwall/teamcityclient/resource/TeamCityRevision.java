package net.awired.visuwall.teamcityclient.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "revision")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityRevision {

	@XmlAttribute(name = "display-version")
	private int displayVersion;

	@XmlElement(name = "vcs-root")
	private TeamCityVcsRoot vcsRoot;

	public int getDisplayVersion() {
		return displayVersion;
	}

	public void setDisplayVersion(int displayVersion) {
		this.displayVersion = displayVersion;
	}

	public TeamCityVcsRoot getVcsRoot() {
		return vcsRoot;
	}

	public void setVcsRoot(TeamCityVcsRoot vcsRoot) {
		this.vcsRoot = vcsRoot;
	}

}
