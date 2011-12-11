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

package net.awired.visuwall.plugin.jenkins;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class JenkinsPlugin implements VisuwallPlugin<JenkinsConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPlugin.class);

    @Override
    public JenkinsConnection getConnection(URL url, Map<String, String> properties) {
        JenkinsConnection jenkinsConnectionPlugin = new JenkinsConnection();
        String login = properties.get("login");
        String password = properties.get("password");
        jenkinsConnectionPlugin.connect(url.toString(), login, password);
        return jenkinsConnectionPlugin;
    }

    @Override
    public Class<JenkinsConnection> getConnectionClass() {
        return JenkinsConnection.class;
    }

    @Override
    public String getName() {
        return "Jenkins plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            url = new URL(url.toString() + "/api/");
            String content = Downloadables.getContent(url);
            JenkinsVersionPage jenkinsApiPage = new JenkinsVersionPage(content);
            if (jenkinsApiPage.isJenkinsApiPage()) {
                return jenkinsApiPage.createSoftwareId();
            }
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't get content of " + url, e);
            }
        }
        throw new IncompatibleSoftwareException("Url " + url + " is not compatible with Jenkins");
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

}
