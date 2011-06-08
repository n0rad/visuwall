package net.awired.visuwall.teamcityclient.resource;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "build")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityBuildItem {

	@XmlAttribute
	private String id;

	@XmlAttribute
	private String number;

	@XmlAttribute
	private String status;

	@XmlAttribute
	private String href;

	@XmlAttribute
	private String webUrl;

	@XmlAttribute
	private String buildTypeId;

	@XmlAttribute
	private String startDate;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getNumber() {
		return number;
	}

	public void setNumber(String number) {
		this.number = number;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getHref() {
		return href;
	}

	public void setHref(String href) {
		this.href = href;
	}

	public String getWebUrl() {
		return webUrl;
	}

	public void setWebUrl(String webUrl) {
		this.webUrl = webUrl;
	}

	public String getBuildTypeId() {
		return buildTypeId;
	}

	public void setBuildTypeId(String buildTypeId) {
		this.buildTypeId = buildTypeId;
	}

	public String getStartDate() {
		return startDate;
	}

	public void setStartDate(String startDate) {
		this.startDate = startDate;
	}
}
