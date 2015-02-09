/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fr.norad.visuwall.providers.hudson;

public class HudsonUrlBuilder {

    private final String hudsonUrl;

    private static final String API_XML = "/api/xml";
    private static final String ALL_JOBS_URI = "";
    private static final String JOB_URI = "/job";
    private static final String USER_URI = "/user";
    private static final String VIEW_URI = "/view";

    public HudsonUrlBuilder(String hudsonUrl) {
        this.hudsonUrl = hudsonUrl;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return Hudson Url to obtain test informations of a job
     */
    public String getTestResultUrl(String jobName, int buildNumber) {
        return hudsonUrl + JOB_URI + "/" + encode(jobName) + "/" + buildNumber + "/testReport" + API_XML;
    }

    public String getPomUrl(String jobName) {
        return hudsonUrl + JOB_URI + "/" + encode(jobName) + "/ws/pom.xml";
    }

    /**
     * @return Hudson Url to obtain all jobs
     */
    public String getAllProjectsUrl() {
        return hudsonUrl + ALL_JOBS_URI + API_XML;
    }

    /**
     * @param jobName
     * @return Hudson Url to obtain informations of a job
     */
    public String getJobUrl(String jobName) {
        return hudsonUrl + JOB_URI + "/" + encode(jobName) + API_XML;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return Hudson Url to obtain detailed informations of a job
     */
    public String getBuildUrl(String jobName, int buildNumber) {
        return hudsonUrl + JOB_URI + "/" + encode(jobName) + "/" + buildNumber + API_XML;
    }

    private String encode(String url) {
        return url.replaceAll(" ", "%20");
    }

    public String getUserUrl(String userName) {
        return hudsonUrl + USER_URI + "/" + encode(userName) + API_XML;
    }

    public String getViewUrl(String viewName) {
        return hudsonUrl + VIEW_URI + "/" + encode(viewName) + API_XML;
    }
}
