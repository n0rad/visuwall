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

package net.awired.visuwall.plugin.hudson;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Properties;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class HudsonPlugin implements VisuwallPlugin<HudsonConnectionPlugin> {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonPlugin.class);

    @Override
    public HudsonConnectionPlugin getConnection(String url, Properties info) {
        HudsonConnectionPlugin hudsonConnectionPlugin = new HudsonConnectionPlugin();
        hudsonConnectionPlugin.connect(url);
        return hudsonConnectionPlugin;
    }

    @Override
    public Class<HudsonConnectionPlugin> getConnectionClass() {
        return HudsonConnectionPlugin.class;
    }

    @Override
    public String getName() {
        return "Hudson plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        Preconditions.checkNotNull(url, "url is mandatory");
        String xml = getContent(url);
        if (isManageable(xml)) {
            return createSoftwareId(xml);
        }
        throw new IncompatibleSoftwareException("Url " + url + " is not compatible with Hudson");
    }

    private SoftwareId createSoftwareId(String xml) {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Hudson");
        String strVersion = getVersion(xml);
        softwareId.setVersion(strVersion);
        return softwareId;
    }

    private String getVersion(String xml) {
        return new HudsonVersionExtractor(xml).version();
    }

    private boolean isManageable(String xml) {
        return xml.contains("Remote API [Hudson]");
    }

    private String getContent(URL url) {
        InputStream stream = null;
        try {
            url = new URL(url.toString() + "/api/");
            stream = url.openStream();
            byte[] content = ByteStreams.toByteArray(stream);
            String xml = new String(content);
            return xml;
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't get content of " + url, e);
            }
            return "";
        } finally {
            Closeables.closeQuietly(stream);
        }
    }
}
