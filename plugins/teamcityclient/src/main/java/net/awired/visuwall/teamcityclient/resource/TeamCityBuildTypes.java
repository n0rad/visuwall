package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "buildTypes")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityBuildTypes {

	@XmlElements(value = { @XmlElement(name = "buildType") })
	private List<TeamCityBuildType> buildTypes = new ArrayList<TeamCityBuildType>();

	public List<TeamCityBuildType> getBuildTypes() {
		return buildTypes;
	}

	public void setBuildTypes(List<TeamCityBuildType> buildTypes) {
		this.buildTypes = buildTypes;
	}
}
