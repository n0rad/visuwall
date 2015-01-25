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
package fr.norad.visuwall.plugin.deployit;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.domain.SoftwareId;
import fr.norad.visuwall.exception.ConnectionException;
import fr.norad.visuwall.exception.SoftwareNotFoundException;
import fr.norad.visuwall.plugin.VisuwallPlugin;
import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.GenericSoftwareClientFactory;
import fr.norad.visuwall.providers.deployit.resource.RepositoryObjectIds;

public class DeployItPlugin implements VisuwallPlugin<DeployItConnection> {

    private final static Logger LOG = LoggerFactory.getLogger(DeployItPlugin.class);

    private GenericSoftwareClient client;
    private GenericSoftwareClientFactory factory;

    public DeployItPlugin() {
        LOG.info("DeployIt plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public DeployItConnection getConnection(URL url, Map<String, String> properties) throws ConnectionException {
        DeployItConnection connectionPlugin = new DeployItConnection();
        connectionPlugin.connect(url.toString(), properties.get("login"), properties.get("password"));
        return connectionPlugin;
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        Map<String, String> properties = new HashMap<String, String>();
        return properties;
    }

    @Override
    public Class<DeployItConnection> getConnectionClass() {
        return DeployItConnection.class;
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public String getName() {
        return "DeployIt Plugin";
    }

    @Override
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        client = factory.createClient(properties);
        if (isDeployIt(url.toString())) {
            return createSoftwareId();
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with DeployIt");
    }

    private SoftwareId createSoftwareId() {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("DeployIt");
        softwareId.setVersion("unknown");
        softwareId.setWarnings("");
        return softwareId;
    }

    private boolean isDeployIt(String url) {
        String serverUrl = url + "/deployit/query";
        return client.exist(serverUrl, RepositoryObjectIds.class);
    }
}
