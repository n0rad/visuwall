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
package fr.norad.visuwall.plugin.hudson;

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

public class HudsonPlugin implements VisuwallPlugin<HudsonConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonPlugin.class);

    private GenericSoftwareClientFactory factory;

    public HudsonPlugin() {
        LOG.info("Hudson plugin loaded.");
        factory = new GenericSoftwareClientFactory();
    }

    @Override
    public HudsonConnection getConnection(URL url, Map<String, String> properties) {
        HudsonConnection hudsonConnectionPlugin = new HudsonConnection();
        String login = properties.get("login");
        String password = properties.get("password");
        hudsonConnectionPlugin.connect(url.toString(), login, password);
        return hudsonConnectionPlugin;
    }

    @Override
    public Class<HudsonConnection> getConnectionClass() {
        return HudsonConnection.class;
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
    public SoftwareId getSoftwareId(URL url, Map<String, String> properties) throws SoftwareNotFoundException {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (properties == null) {
            properties = getPropertiesWithDefaultValue();
        }
        try {
            GenericSoftwareClient client = factory.createClient(properties);
            URL apiUrl = new URL(url.toString() + "/api/");
            String xml = client.download(apiUrl);
            if (isManageable(xml)) {
                return createSoftwareId(xml);
            }
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson, content: " + xml);
        } catch (MalformedURLException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson", e);
        } catch (ResourceNotFoundException e) {
            throw new SoftwareNotFoundException("Url " + url + " is not compatible with Hudson", e);
        }
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
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

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        return new HashMap<String, String>();
    }

}
