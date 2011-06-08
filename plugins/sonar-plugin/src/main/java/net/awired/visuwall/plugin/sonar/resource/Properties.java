package net.awired.visuwall.plugin.sonar.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "properties")
@XmlAccessorType(XmlAccessType.FIELD)
public class Properties {

	@XmlElements({ @XmlElement(name = "property") })
	private List<Property> properties = new ArrayList<Property>();

	public List<Property> getProperties() {
		return properties;
	}

	public void setProperties(List<Property> properties) {
		this.properties = properties;
	}

}
