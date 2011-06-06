package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityProperties {

	@XmlElements(@XmlElement(name = "property"))
	private List<TeamCityProperty> properties = new ArrayList<TeamCityProperty>();

	public List<TeamCityProperty> getProperties() {
		return properties;
	}

	public void setProperties(List<TeamCityProperty> properties) {
		this.properties = properties;
	}

}
