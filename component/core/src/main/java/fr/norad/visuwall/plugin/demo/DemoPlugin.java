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
package fr.norad.visuwall.plugin.demo;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;
import fr.norad.visuwall.api.plugin.VisuwallPlugin;

public class DemoPlugin implements VisuwallPlugin<DemoConnection> {

    private static final String DEMO_VISUWALL_CI = "http://demo.visuwall.ci";

    @Override
    public DemoConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        if (url == null || !DEMO_VISUWALL_CI.equals(url.toString())) {
            throw new ConnectionException(getName() + " is not compatible with url : " + url);
        }
        DemoConnection connection = new DemoConnection();
        connection.connect(url.toString(), null, null);
        return connection;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<DemoConnection> getConnectionClass() {
        return DemoConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "Demo Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        if (url == null || !DEMO_VISUWALL_CI.equals(url.toString())) {
            throw new SoftwareNotFoundException(getName() + " is not compatible with url : " + url);
        }
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("demo");
        softwareId.setCompatible(true);
        softwareId.setVersion("1.0");
        softwareId.setWarnings("This is a demo plugin");
        return softwareId;
    }

}
