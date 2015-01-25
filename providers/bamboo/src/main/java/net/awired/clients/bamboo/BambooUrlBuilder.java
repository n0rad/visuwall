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

package net.awired.clients.bamboo;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class BambooUrlBuilder {

    private String bambooUrl;

    private static final Logger LOG = LoggerFactory.getLogger(BambooUrlBuilder.class);

    BambooUrlBuilder(String bambooUrl) {
        this.bambooUrl = bambooUrl + "/rest/api/";
    }

    String getAllPlansUrl() {
        return build("/plan?max-result=1000");
    }

    String getAllBuildsUrl() {
        return build("/build");
    }

    String getAllResultsUrl() {
        return build("/result");
    }

    String getPlanUrl(String planKey) {
        return build("/plan/" + planKey);
    }


    String getResultUrl(String planKey, int buildNumber) {
        return build("/result/" + planKey + "-" + buildNumber
                + "?expand=changes,metadata,stages,labels,jiraIssues,comments");
    }

    String getResultsUrl(String planKey) {
        return build("/result/" + planKey);
    }

    private String build(String uri) {
        String url = bambooUrl + "latest" + uri;
        if (LOG.isDebugEnabled()) {
            LOG.debug(url);
        }
        return url;
    }

}
