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

package fr.norad.visuwall.plugin.teamcity;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class TestResultExtractor {

    private static final Logger LOG = LoggerFactory.getLogger(TestResultExtractor.class);

    public static int extractFailed(String statusText) {
        String key = "failed";
        return extract(statusText, key);
    }

    private static int extract(String statusText, String key) {
        String[] split = statusText.split(",");
        for (String s : split) {
            if (s.contains(key)) {
                String[] split2 = s.split(":");
                if (split2.length > 1) {
                    String t = split2[1].trim();
                    int indexOfBlank = t.indexOf(" ");
                    if (indexOfBlank != -1) {
                        String substring = t.substring(0, indexOfBlank);
                        int parseInt = Integer.parseInt(substring);
                        return parseInt;
                    } else {
                        return Integer.parseInt(t);
                    }
                } else {
                    LOG.warn("statusText is invalid: '" + statusText + "'");
                }
            }
        }
        return 0;
    }

    public static int extractPassed(String statusText) {
        String key = "passed";
        return extract(statusText, key);
    }

    public static int extractIgnored(String statusText) {
        String key = "ignored";
        return extract(statusText, key);

    }

}
