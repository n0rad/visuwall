package net.awired.visuwall.bambooclient.builder;

public class BambooUrlBuilder {

    private String bambooUrl;

    public BambooUrlBuilder(String bambooUrl) {
        this.bambooUrl = bambooUrl + "/rest/api/";
    }

    public String getAllProjectsUrl() {
        return bambooUrl + "latest/plan";
    }

    public String getProjectsUrl(String projectName) {
        return bambooUrl+"latest/result/"+projectName+ "?expand=results.result";
    }

    public String getBuildUrl(String projectName, int buildNumber) {
        return bambooUrl+"latest/result/"+projectName+"-"+buildNumber+"?expand=changes,metadata,stages,labels,jiraIssues,comments";
    }

    public String getLatestBuildResult(String projectName) {
        return bambooUrl+"latest/result/"+projectName;
    }

    public String getIsBuildingUrl(String projectName) {
        return bambooUrl+"latest/plan/"+projectName;
    }

    public String getLastBuildUrl() {
        return bambooUrl + "latest/build";
    }
}
