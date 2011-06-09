package net.awired.visuwall.hudsonclient.generated.hudson;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;

@XmlRootElement(name = "listView")
@XmlAccessorType(XmlAccessType.FIELD)
public class HudsonView {

	@XmlElements({ @XmlElement(name = "job") })
	private List<HudsonModelJob> jobs = new ArrayList<HudsonModelJob>();

	private String name;

	private String url;

	public List<HudsonModelJob> getJobs() {
		return jobs;
	}

	public void setJobs(List<HudsonModelJob> jobs) {
		this.jobs = jobs;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

}
