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
		checkProjectId(projectId);
		return build("/projects/id:" + projectId);
	}

	public String getBuildType(String buildTypeId) {
		Preconditions.checkNotNull(buildTypeId, "buildTypeId is mandatory");
		return build("/buildTypes/id:" + buildTypeId);
	}

	public String getBuild(int buildId) {
		Preconditions.checkArgument(buildId >= 0, "buildId must be >= 0");
		return build("/builds/id:" + buildId);
	}

	private String build(String url) {
		return teamCityUrl + API_URI + url;
	}

	public String getBuildList(int buildTypeId) {
		return build("/buildTypes/id:bt" + buildTypeId + "/builds");
	}

	private void checkProjectId(String projectId) {
		Preconditions.checkNotNull(projectId, "projectId is mandatory");
	}
}
