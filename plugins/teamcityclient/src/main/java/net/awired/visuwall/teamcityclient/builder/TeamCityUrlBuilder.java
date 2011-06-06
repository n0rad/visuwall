package net.awired.visuwall.teamcityclient.builder;

import com.google.common.base.Preconditions;

public class TeamCityUrlBuilder {

	private String teamCityUrl;
	private static final String API_URI = "/app/rest";

	public TeamCityUrlBuilder(String teamCityUrl) {
		Preconditions.checkNotNull(teamCityUrl, "teamCityUrl is mandatory");
		this.teamCityUrl = teamCityUrl;
	}

	public String getProjects() {
		return build("/projects");
	}

	public String getProject(String projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
		return build("/projects/id:" + projectId);
	}

	public String getBuildType(String buildTypeId) {
		Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
		return build("/buildTypes/id:" + buildTypeId);
	}

	private String build(String url) {
		return teamCityUrl + API_URI + url;
	}

}
