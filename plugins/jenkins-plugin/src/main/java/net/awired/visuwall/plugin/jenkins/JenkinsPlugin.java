/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
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
package net.awired.visuwall.plugin.jenkins;

import java.io.IOException;
import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.plugin.ConnectionPlugin;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;

public class JenkinsPlugin implements VisuwallPlugin {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPlugin.class);

    @Override
    public ConnectionPlugin getConnection(String url, Properties info) {
        JenkinsConnectionPlugin jenkinsConnectionPlugin = new JenkinsConnectionPlugin();
        jenkinsConnectionPlugin.connect(url);
        return jenkinsConnectionPlugin;
    }

    @Override
    public String getName() {
        return "Jenkins";
    }

    @Override
    public int getVersion() {
        return 1;
    }

    @Override
    public boolean isManageable(URL url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (url.getHost().contains("builds.apache.org")) {
            return true;
        }
        try {
            url = new URL(url.toString() + "/api");
            byte[] content = ByteStreams.toByteArray(url.openStream());
            String xml = new String(content);
            return xml.contains("Remote API [Jenkins]");
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(url + " is not an jenkins instance ", e);
            }
            return false;
        }
    }
}