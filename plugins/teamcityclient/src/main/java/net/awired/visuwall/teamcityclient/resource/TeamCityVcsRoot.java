package net.awired.visuwall.teamcityclient.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "vcs-root")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityVcsRoot {

	@XmlAttribute
	private String href;

	@XmlAttribute
	private String name;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
