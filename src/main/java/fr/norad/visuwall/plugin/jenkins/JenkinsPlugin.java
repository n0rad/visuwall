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
package fr.norad.visuwall.plugin.jenkins;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.domain.SoftwareId;
import fr.norad.visuwall.exception.SoftwareNotFoundException;
import fr.norad.visuwall.plugin.VisuwallPlugin;
import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.GenericSoftwareClientFactory;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;

public class JenkinsPlugin implements VisuwallPlugin<JenkinsConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(JenkinsPlugin.class);

    private GenericSoftwareClientFactory factory;

    public JenkinsPlugin() {
        LOG.info("Jenkins plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

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
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (properties == null) {
            properties = getPropertiesWithDefaultValue();
        }
        try {
            GenericSoftwareClient client = factory.createClient(properties);
            URL apiUrl = new URL(url.toString() + "/api/");
            String xml = client.download(apiUrl);
            JenkinsVersionPage jenkinsApiPage = new JenkinsVersionPage(xml);
            if (jenkinsApiPage.isJenkinsApiPage()) {
                return jenkinsApiPage.createSoftwareId();
            }
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins", e);
        } catch (MalformedURLException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins", e);
        }
        throw new SoftwareNotFoundException("Url " + url + " is not compatible with Jenkins");
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
