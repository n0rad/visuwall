package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityTags {

	@XmlElements({ @XmlElement(name = "tag") })
	private List<TeamCityTag> tags = new ArrayList<TeamCityTag>();

	public List<TeamCityTag> getTags() {
		return tags;
	}

	public void setTags(List<TeamCityTag> tags) {
		this.tags = tags;
	}
}
