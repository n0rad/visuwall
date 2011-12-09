/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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

package net.awired.clients.teamcity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TeamCityUrlBuilder {

    private String teamCityUrl;
    private static final String API_URI = "/app/rest";

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityUrlBuilder.class);

    TeamCityUrlBuilder(String teamCityUrl) {
        this.teamCityUrl = teamCityUrl;
    }

    String getProjects() {
        return build("/projects");
    }

    String getProject(String projectId) {
        return build("/projects/id:" + projectId);
    }

    String getBuildType(String buildTypeId) {
        return build("/buildTypes/id:" + buildTypeId);
    }

    String getBuild(int buildId) {
        return build("/builds/id:" + buildId);
    }

    String getRunningBuild() {
        return build("/builds/running:true");
    }

    String getChanges(int buildId) {
        return build("/changes?build=id:" + buildId);
    }

    String getChange(String changeId) {
        return build("/changes/id:" + changeId);
    }

    String getVersion() {
        return build("/version");
    }

    String getServer() {
        return build("/server");
    }

    private String build(String url) {
        String finalUrl = teamCityUrl + API_URI + url;
        if (LOG.isDebugEnabled()) {
            LOG.debug(finalUrl);
        }
        return finalUrl;
    }

    String getBuildList(String buildTypeId) {
        return build("/buildTypes/id:" + buildTypeId + "/builds");
    }

    String getPomUrl(int buildId) {
        return teamCityUrl + "/builds/id:" + buildId + "/pom.xml";
    }
}
