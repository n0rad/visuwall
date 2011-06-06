package net.awired.visuwall.teamcityclient.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "changes")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityChanges {

	@XmlAttribute
	private String href;

	@XmlAttribute
	private int count;

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}

}
