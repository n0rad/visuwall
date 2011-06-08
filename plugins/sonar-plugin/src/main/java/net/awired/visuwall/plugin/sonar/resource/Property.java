package net.awired.visuwall.plugin.sonar.resource;

import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "property")
public class Property {

	private String key;

	private String value;

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isKey(String key) {
		if (key == null || this.key == null)
			return false;
		return key.equals(this.key);
	}

}
