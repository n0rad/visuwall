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

package fr.norad.visuwall.plugin.jenkins;

import static org.apache.commons.lang.StringUtils.isNumeric;
import fr.norad.visuwall.api.domain.SoftwareId;

import org.apache.commons.lang.StringUtils;

public class JenkinsVersionPage {

    private String content;

    public JenkinsVersionPage(String content) {
        this.content = content;
    }

    public SoftwareId createSoftwareId() {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Jenkins");
        String strVersion = getVersion(content);
        softwareId.setVersion(strVersion);
        addWarnings(softwareId, strVersion);
        return softwareId;
    }

    private void addWarnings(SoftwareId softwareInfo, String strVersion) {
        String cleanedVersion = StringUtils.remove(strVersion, ".");
        if (isNumeric(cleanedVersion)) {
            double version = Double.parseDouble(strVersion);
            if (version < 1.405) {
                addWarningForVersionBefore1405(softwareInfo);
            }
        }
    }

    private void addWarningForVersionBefore1405(SoftwareId softwareInfo) {
        softwareInfo.setWarnings("This jenkins version has a bug with git project. Git project wont be display.");
    }

    private String getVersion(String xml) {
        return new JenkinsVersionExtractor(xml).version();
    }

    public boolean isJenkinsApiPage() {
        return content.contains("Remote API [Jenkins]");
    }

}
