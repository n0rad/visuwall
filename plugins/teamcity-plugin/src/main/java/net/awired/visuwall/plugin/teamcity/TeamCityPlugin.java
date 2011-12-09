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
import java.util.HashMap;
import java.util.Map;

import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.teamcity.resource.TeamCityServer;
import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Objects;
import com.google.common.base.Preconditions;

public class TeamCityPlugin implements VisuwallPlugin<TeamCityConnection> {

    @VisibleForTesting
    GenericSoftwareClient genericSoftwareClient = new GenericSoftwareClient("guest", "");

    @Override
    public TeamCityConnection getConnection(URL url, Map<String, String> properties) {
        TeamCityConnection connectionPlugin = new TeamCityConnection();
        connectionPlugin.connect(url.toString(), properties.get("login"), properties.get("password"));
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
        try {
            TeamCityServer teamCityServer = getServer(url.toString());
            return createSoftwareId(teamCityServer);
        } catch (ResourceNotFoundException e) {
            throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity", e);
        }
    }

    private SoftwareId createSoftwareId(TeamCityServer teamCityServer) throws ResourceNotFoundException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("TeamCity");
        String strVersion = getVersion(teamCityServer);
        softwareId.setVersion(strVersion);
        softwareId.setWarnings("");
        return softwareId;
    }

    private TeamCityServer getServer(String url) throws ResourceNotFoundException {
        String serverUrl = url + "/app/rest/server";
        TeamCityServer server = genericSoftwareClient.resource(serverUrl, TeamCityServer.class);
        return server;
    }

    private String getVersion(TeamCityServer server) throws ResourceNotFoundException {
        return server.getVersionMajor() + "." + server.getVersionMinor();
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("name", getName()) //
                .add("version", getVersion()).toString();
    }

    @Override
    public Map<String, String> getPropertiesWithDefaultValue() {
        Map<String, String> defaultValues = new HashMap<String, String>();
        defaultValues.put("login", "guest");
        defaultValues.put("password", "");
        return defaultValues;
    }

}
