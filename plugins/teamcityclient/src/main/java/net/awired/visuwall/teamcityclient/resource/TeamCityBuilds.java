package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "builds")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityBuilds {

	@XmlAttribute
	private String nextHref;

	@XmlAttribute
	private int count;

	@XmlElements({ @XmlElement(name = "build") })
	private List<TeamCityBuildItem> builds = new ArrayList<TeamCityBuildItem>();

	public String getNextHref() {
		return nextHref;
	}

	public void setNextHref(String nextHref) {
		this.nextHref = nextHref;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

	public List<TeamCityBuildItem> getBuilds() {
		return builds;
	}

	public void setBuilds(List<TeamCityBuildItem> builds) {
		this.builds = builds;
	}

}
