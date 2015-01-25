/**
 *
 *     Copyright (C) norad.fr
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

class TestResultExtractor {

    private StatusTextValueExtractor statusTextValueExtractor = new StatusTextValueExtractor();

    public int extractFailed(String statusText) {
        return statusTextValueExtractor.extract(statusText, "failed") + statusTextValueExtractor.extract(statusText, "errors");
    }

    public int extractPassed(String statusText) {
        return statusTextValueExtractor.extract(statusText, "passed");
    }

    public int extractIgnored(String statusText) {
        return statusTextValueExtractor.extract(statusText, "ignored");
    }

}
