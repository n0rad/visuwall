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
package fr.norad.visuwall.plugin.sonar;

import java.io.IOException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.io.ByteStreams;

public class SonarVersionExtractor {

    private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

    public String propertiesVersion(Properties properties) {
        for (Property property : properties.getProperties()) {
            if (property.isKey(SONAR_CORE_VERSION_KEY)) {
                return property.getValue();
            }
        }
        return "unknown";
    }

    public String welcomePageVersion(URL url) {
        try {
            byte[] byteArray = ByteStreams.toByteArray(url.openStream());
            String htmlContent = new String(byteArray);
            Pattern p = Pattern.compile(".* - v\\.([0-9]\\.[0-9]*.[0-9]*) - .*");
            Matcher m = p.matcher(htmlContent);
            while (m.find()) {
                return m.group(1);
            }
        } catch (IOException e) {
            return "unknown";
        }
        return "unknown";
    }

}
