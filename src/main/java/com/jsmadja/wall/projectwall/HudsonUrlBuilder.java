/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.jsmadja.wall.projectwall;

public class HudsonUrlBuilder {

    private final String hudsonUrl;

    private static final String API_XML = "/api/xml";
    private static final String ALL_JOBS_URI = "";
    private static final String JOB_URI = "/job";

    public HudsonUrlBuilder(String hudsonUrl) {
        this.hudsonUrl = hudsonUrl;
    }

    /**
     * @return Hudson Url to obtain all jobs
     */
    public final String getAllJobsUrl() {
        return  hudsonUrl+ALL_JOBS_URI+API_XML;
    }

    /**
     * @param jobName
     * @return Hudson Url to obtain informations of a job
     */
    public final String getJobUrl(String jobName) {
        return hudsonUrl+JOB_URI+"/"+jobName+API_XML;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return Hudson Url to obtain detailed informations of a job
     */
    public final String getJobUrl(String jobName, int buildNumber) {
        return hudsonUrl+JOB_URI+"/"+jobName+"/"+buildNumber+API_XML;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return Hudson Url to obtain test informations of a job
     */
    public final String getTestResultUrl(String jobName, int buildNumber) {
        return hudsonUrl+JOB_URI+"/"+jobName+"/"+buildNumber+"/testReport"+API_XML;

    }

}
