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
package fr.norad.visuwall.plugin.continuum;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import fr.norad.visuwall.api.domain.SoftwareId;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.SoftwareNotFoundException;
import fr.norad.visuwall.api.plugin.VisuwallPlugin;

public class ContinuumPlugin implements VisuwallPlugin<ContinuumConnection> {

    @Override
    public ContinuumConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        ContinuumConnection continuumConnection = new ContinuumConnection();
        continuumConnection.connect(url.toString(), "", "");
        return continuumConnection;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

    @Override
    public Class<ContinuumConnection> getConnectionClass() {
        return ContinuumConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "Continumm Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        if (isManageable(url)) {
            SoftwareId softwareId = new SoftwareId();
            softwareId.setName("Continuum");
            softwareId.setVersion("1.0");
            softwareId.setWarnings("");
            return softwareId;
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with Continuum");
    }

    private boolean isManageable(URL url) {
        try {
            url = new URL(url.toString() + "/groupSummary.action");
            String content;
            content = Downloadables.getContent(url);
            return content.contains("Continuum");
        } catch (IOException e) {
            return false;
        }
    }
}
