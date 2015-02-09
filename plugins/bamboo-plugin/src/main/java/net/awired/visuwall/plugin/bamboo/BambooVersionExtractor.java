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

package fr.norad.visuwall.plugin.bamboo;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

class BambooVersionExtractor {

    private BambooVersionExtractor() {
    }

    static String extractVersion(URL url) throws BambooVersionNotFoundException {
        InputStream stream = null;
        try {
            stream = url.openStream();
            byte[] bytes = ByteStreams.toByteArray(stream);
            String page = new String(bytes);
            return extractVersion(page);
        } catch (IOException e) {
            throw new BambooVersionNotFoundException("Can't extract version from url:" + url, e);
        } finally {
            Closeables.closeQuietly(stream);
        }
    }

    static String extractVersion(String content) throws BambooVersionNotFoundException {
        Preconditions.checkNotNull(content, "content is mandatory");
        if (content.contains("version ") && content.contains(" build")) {
            String rightOfVersionToken = content.split("version ")[1];
            String leftOfBuildToken = rightOfVersionToken.split(" build")[0];
            return leftOfBuildToken;
        }
        throw new BambooVersionNotFoundException("Can't extract version from content: " + content);
    }
}
