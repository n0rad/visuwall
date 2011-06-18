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

package net.awired.visuwall.plugin.teamcity;

import java.net.URL;
import java.util.Properties;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;
import net.awired.visuwall.teamcityclient.resource.TeamCityServer;

import com.google.common.base.Preconditions;

public class TeamCityPlugin implements VisuwallPlugin<TeamCityConnection> {

	private GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient("guest", "");

    @Override
    public TeamCityConnection getConnection(String url, Properties info) {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect(url);
        return connectionPlugin;
    }

    @Override
    public Class<TeamCityConnection> getConnectionClass() {
        return TeamCityConnection.class;
    }

    @Override
    public String getName() {
        return "TeamCity plugin";
    }

    @Override
    public float getVersion() {
        return 1.0f;
    }

    @Override
    public SoftwareId getSoftwareId(URL url) throws IncompatibleSoftwareException {
        Preconditions.checkNotNull(url, "url is mandatory");
		if (isManageable(url.toString())) {
            try {
                return createSoftwareId(url);
            } catch (ResourceNotFoundException e) {
				throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity", e);
            }
        }
        throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity");
    }

	private boolean isManageable(String url) {
		try {
			getVersion(url);
		} catch (ResourceNotFoundException e) {
			return false;
		}
		return true;
	}

    private SoftwareId createSoftwareId(URL url) throws ResourceNotFoundException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("TeamCity");
		String strVersion = getVersion(url.toString());
		softwareId.setVersion(strVersion);
        return softwareId;
    }

	private String getVersion(String url) throws ResourceNotFoundException {
		TeamCityUrlBuilder builder = new TeamCityUrlBuilder(url);
		String serverUrl = builder.getServer();
		TeamCityServer server = genericSoftwareClient.resource(serverUrl, TeamCityServer.class);
		return server.getVersionMajor() + "." + server.getVersionMinor();
    }

}
