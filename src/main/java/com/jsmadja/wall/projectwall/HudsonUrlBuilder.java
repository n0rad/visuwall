package com.jsmadja.wall.projectwall;

public class HudsonUrlBuilder {

    private final String hudsonUrl;

    private static final String API_XML = "/api/xml";
    private static final String ALL_JOBS_URI = "";
    private static final String JOB_URI = "/job";

    public HudsonUrlBuilder(String hudsonUrl) {
        this.hudsonUrl = hudsonUrl;
    }

    public String getAllJobsUrl() {
        return  hudsonUrl+ALL_JOBS_URI+API_XML;
    }

    public String getJobUrl(String jobName) {
        return hudsonUrl+JOB_URI+"/"+jobName+API_XML;
    }

    public String getJobUrl(String jobName, int buildNumber) {
        return hudsonUrl+JOB_URI+"/"+jobName+"/"+buildNumber+API_XML;
    }

}
