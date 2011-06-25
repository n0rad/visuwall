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
import java.util.Properties;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import com.google.common.base.Preconditions;

public class BambooPlugin implements VisuwallPlugin<BambooConnection> {

    @Override
    public BambooConnection getConnection(String url, Properties info) {
		BambooConnection connection = new BambooConnection();
		connection.connect(url);
		return connection;
    }

    @Override
    public Class<BambooConnection> getConnectionClass() {
        return BambooConnection.class;
    }

    @Override
    public String getName() {
        return "Bamboo Plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("Bamboo");
        try {
            softwareId.setVersion(BambooVersionExtractor.extractVersion(url));
        } catch (BambooVersionNotFoundException e) {
            softwareId.setVersion("version not found");
        }
        return softwareId;
    }

}
