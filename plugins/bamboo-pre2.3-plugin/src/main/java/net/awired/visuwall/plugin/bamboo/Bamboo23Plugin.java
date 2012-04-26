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

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.SoftwareNotFoundException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class Bamboo23Plugin implements VisuwallPlugin<Bamboo23Connection> {

    private static final Logger LOG = LoggerFactory.getLogger(Bamboo23Plugin.class);

    public Bamboo23Plugin() {
        LOG.info("Bamboo 23 plugin loaded.");
    }

    @Override
    public Bamboo23Connection getConnection(URL url, Map<String, String> properties) {
        Bamboo23Connection connection = new Bamboo23Connection();
        String login = getPropertiesWithDefaultValue().get("login");
        String password = getPropertiesWithDefaultValue().get("password");
        if (StringUtils.isNotBlank(properties.get("login"))) {
            login = properties.get("login");
            password = properties.get("password");
        }
        connection.connect(url.toString(), login, password);
        return connection;
    }

    @Override
    public Class<Bamboo23Connection> getConnectionClass() {
        return Bamboo23Connection.class;
    }

    @Override
    public String getName() {
        return "Bamboo 23 Plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            String version = Bamboo23VersionExtractor.extractVersion(url);
            float versionAsFloat = Float.valueOf(version.replaceFirst("\\.", "#").replaceAll("\\.", "")
                    .replace("#", "."));
            if (versionAsFloat > 2.3) {
                throw new SoftwareNotFoundException("This plugin is not compatible with Bamboo 23");
            }
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Bamboo");
            softwareId.setVersion(version);
            softwareId.setWarnings("Bamboo with version <= 2.3 has limited features in Visuwall");
            return softwareId;
        } catch (Bamboo23VersionNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Bamboo 23");
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        HashMap<String, String> properties = new HashMap<String, String>();
        properties.put("login", "admin");
        properties.put("password", "password");
        return properties;
    }
}
