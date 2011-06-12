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

package net.awired.visuwall.plugin.sonar;

import java.net.URL;
import javax.ws.rs.core.MediaType;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.plugin.sonar.resource.Properties;
import net.awired.visuwall.plugin.sonar.resource.Property;
import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;

public class SonarPlugin implements VisuwallPlugin<SonarConnectionPlugin> {

    private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

    @Override
    public SonarConnectionPlugin getConnection(String url, java.util.Properties info) {
        SonarConnectionPlugin sonarConnectionPlugin = new SonarConnectionPlugin();
        sonarConnectionPlugin.connect(url);
        return sonarConnectionPlugin;
    }

    @Override
    public Class<SonarConnectionPlugin> getConnectionClass() {
        return SonarConnectionPlugin.class;
    }

    @Override
    public String getName() {
        return "Sonar plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        Preconditions.checkNotNull(url, "url is mandatory");
        try {
            Client client = Client.create();
            WebResource resource = client.resource(url.toString() + "/api/properties");
            Properties properties = resource.accept(MediaType.APPLICATION_XML).get(Properties.class);
            if (isManageable(properties)) {
                SoftwareId softwareId = new SoftwareId();
                softwareId.setName("Sonar");
                softwareId.setVersion(getVersion(properties));
                return softwareId;
            }
            throw new IncompatibleSoftwareException("Url " + url + " is not compatible with Sonar");
        } catch (UniformInterfaceException e) {
            throw new IncompatibleSoftwareException("Url " + url + " is not compatible with Sonar");
        }
    }

    private String getVersion(Properties properties) {
        return new SonarVersionExtractor(properties).version();
    }

    private boolean isManageable(Properties properties) {
        for (Property property : properties.getProperties()) {
            if (property.isKey(SONAR_CORE_VERSION_KEY)) {
                return true;
            }
        }
        return false;
    }

}
