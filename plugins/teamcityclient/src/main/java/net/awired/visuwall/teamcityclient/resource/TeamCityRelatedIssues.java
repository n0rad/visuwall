package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "relatedIssues")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityRelatedIssues {

	@XmlElements({ @XmlElement(name = "relatedIssue") })
	private List<TeamCityRelatedIssue> relatedIssues = new ArrayList<TeamCityRelatedIssue>();

	public List<TeamCityRelatedIssue> getRelatedIssues() {
		return relatedIssues;
	}

	public void setRelatedIssues(List<TeamCityRelatedIssue> relatedIssues) {
		this.relatedIssues = relatedIssues;
	}

}
