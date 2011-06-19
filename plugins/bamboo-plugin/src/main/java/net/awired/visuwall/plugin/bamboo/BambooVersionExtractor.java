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

package net.awired.visuwall.plugin.bamboo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import com.google.common.io.ByteStreams;

public class BambooVersionExtractor {

    public static String extractVersion(URL url) throws BambooVersionNotFoundException {
        try {
            InputStream stream = url.openStream();
            byte[] bytes = ByteStreams.toByteArray(stream);
            String page = new String(bytes);
            if (page.contains("version ") && page.contains(" build")) {
                return page.split("version ")[1].split(" build")[0];
            }
        } catch (IOException e) {
            throw new BambooVersionNotFoundException("Can't extract version from url:" + url, e);
        }
        throw new BambooVersionNotFoundException("Can't extract version from url:" + url);
    }

}
