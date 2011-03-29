package net.awired.visuwall.bambooclient.builder;

public class BambooUrlBuilder {

    private String bambooUrl;

    private static final String ALL_PLANS = "/latest/plan";
    private static final String REST = "/rest/api";

    public BambooUrlBuilder(String bambooUrl) {
        this.bambooUrl = bambooUrl + REST;
    }

    public String getAllProjectsUrl() {
        return bambooUrl + ALL_PLANS;
    }

    public String getProjectsUrl(String projectName) {
        return bambooUrl+"/latest/result/"+projectName+ "?expand=results.result";
    }

}
