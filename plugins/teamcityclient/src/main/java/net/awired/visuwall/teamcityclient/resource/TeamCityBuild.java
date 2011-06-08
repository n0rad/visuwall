package net.awired.visuwall.teamcityclient.resource;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import net.awired.visuwall.teamcityclient.DateAdapter;

@XmlRootElement(name = "build")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityBuild {

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
	private boolean personal;

	@XmlAttribute
	private boolean history;

	@XmlAttribute
	private boolean pinned;

	private String statusText;

	private TeamCityBuildType buildType;

	private String startDate;

	private String finishDate;

	private TeamCityAgent agent;

	private TeamCityTags tags;

	private TeamCityProperties properties;

	private TeamCityRevisions revisions;

	private TeamCityVcsRoot vcsRoot;

	private TeamCityChanges changes;

	private TeamCityRelatedIssues relatedIssues;

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

	public boolean isPersonal() {
		return personal;
	}

	public void setPersonal(boolean personal) {
		this.personal = personal;
	}

	public boolean isHistory() {
		return history;
	}

	public void setHistory(boolean history) {
		this.history = history;
	}

	public boolean isPinned() {
		return pinned;
	}

	public void setPinned(boolean pinned) {
		this.pinned = pinned;
	}

	public String getStatusText() {
		return statusText;
	}

	public void setStatusText(String statusText) {
		this.statusText = statusText;
	}

	public TeamCityBuildType getBuildType() {
		return buildType;
	}

	public void setBuildType(TeamCityBuildType buildType) {
		this.buildType = buildType;
	}

	public Date getStartDate() {
		return DateAdapter.parseDate(startDate);
	}

	public Date getFinishDate() {
		return DateAdapter.parseDate(finishDate);
	}

	public TeamCityAgent getAgent() {
		return agent;
	}

	public void setAgent(TeamCityAgent agent) {
		this.agent = agent;
	}

	public List<TeamCityTag> getTags() {
		return tags.getTags();
	}

	public void setTags(TeamCityTags tags) {
		this.tags = tags;
	}

	public List<TeamCityProperty> getProperties() {
		return properties.getProperties();
	}

	public void setProperties(TeamCityProperties properties) {
		this.properties = properties;
	}

	public List<TeamCityRevision> getRevisions() {
		return revisions.getRevisions();
	}

	public void setRevision(TeamCityRevisions revisions) {
		this.revisions = revisions;
	}

	public TeamCityVcsRoot getVcsRoot() {
		return vcsRoot;
	}

	public void setVcsRoot(TeamCityVcsRoot vcsRoot) {
		this.vcsRoot = vcsRoot;
	}

	public TeamCityChanges getChanges() {
		return changes;
	}

	public void setChanges(TeamCityChanges changes) {
		this.changes = changes;
	}

	public List<TeamCityRelatedIssue> getRelatedIssues() {
		return relatedIssues.getRelatedIssues();
	}

	public void setRelatedIssues(TeamCityRelatedIssues relatedIssues) {
		this.relatedIssues = relatedIssues;
	}

}
