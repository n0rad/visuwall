package net.awired.visuwall.teamcityclient.resource;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.visuwall.teamcityclient.DateAdapter;

@XmlRootElement(name = "server")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityServer {

	@XmlAttribute
	private Integer versionMinor;

	@XmlAttribute
	private Integer versionMajor;

	@XmlAttribute
	private String version;

	@XmlAttribute
	private String startTime;

	@XmlAttribute
	private String currentTime;

	@XmlAttribute
	private String buildNumber;

	public Integer getVersionMinor() {
		return versionMinor;
	}

	public void setVersionMinor(Integer versionMinor) {
		this.versionMinor = versionMinor;
	}

	public Integer getVersionMajor() {
		return versionMajor;
	}

	public void setVersionMajor(Integer versionMajor) {
		this.versionMajor = versionMajor;
	}

	public String getVersion() {
		return version;
	}

	public void setVersion(String version) {
		this.version = version;
	}

	public Date getStartTime() {
		return DateAdapter.parseDate(startTime);
	}

	public void setStartTime(String startTime) {
		this.startTime = startTime;
	}

	public Date getCurrentTime() {
		return DateAdapter.parseDate(currentTime);
	}

	public void setCurrentTime(String currentTime) {
		this.currentTime = currentTime;
	}

	public String getBuildNumber() {
		return buildNumber;
	}

	public void setBuildNumber(String buildNumber) {
		this.buildNumber = buildNumber;
	}

}
