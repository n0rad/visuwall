package net.awired.visuwall.teamcityclient.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "revisions")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityRevisions {

	@XmlElements(@XmlElement(name = "revision"))
	private List<TeamCityRevision> revisions = new ArrayList<TeamCityRevision>();

	public List<TeamCityRevision> getRevisions() {
		return revisions;
	}

	public void setRevisions(List<TeamCityRevision> revisions) {
		this.revisions = revisions;
	}
}
