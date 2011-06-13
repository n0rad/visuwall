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

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Properties;

import net.awired.visuwall.api.domain.SoftwareId;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;
import net.awired.visuwall.api.plugin.VisuwallPlugin;
import net.awired.visuwall.teamcityclient.builder.TeamCityUrlBuilder;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Closeables;

public class TeamCityPlugin implements VisuwallPlugin<TeamCityConnection> {

    private static final Logger LOG = LoggerFactory.getLogger(TeamCityPlugin.class);

    @VisibleForTesting
    TeamCityUrlBuilder urlBuilder;

    @Override
    public TeamCityConnection getConnection(String url, Properties info) {
        urlBuilder = new TeamCityUrlBuilder(url);
        TeamCityConnection connectionPlugin = new TeamCityConnection();
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
        String xml = getContent(url);
        System.err.println(xml);
        if (isTeamcityVersionPage(xml)) {
            try {
                return createSoftwareId(url);
            } catch (MalformedURLException e) {
                throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity", e);
            }
        }
        throw new IncompatibleSoftwareException("Url " + url + " is not compatible with TeamCity");
    }

    private SoftwareId createSoftwareId(URL url) throws MalformedURLException {
        SoftwareId softwareId = new SoftwareId();
        softwareId.setName("TeamCity");
        String strVersion = getVersion(url);
        softwareId.setVersion(strVersion);
        return softwareId;
    }

    private String getVersion(URL url) throws MalformedURLException {
        URL versionUrl = new URL(urlBuilder.getVersion());
        String xml = getContent(versionUrl);
        return new TeamCityVersionExtractor(xml).version();
    }

    private boolean isTeamcityVersionPage(String xml) {
        List<String> keywords = Arrays.asList("server", "versionMinor", "versionMajor", "version", "startTime",
                "currentTime", "buildNumber");
        for (String keyword : keywords) {
            if (xml.contains(keyword)) {
                return false;
            }
        }
        return true;
    }

    private String getContent(URL url) {
        InputStream stream = null;
        try {
            stream = url.openStream();
            byte[] content = ByteStreams.toByteArray(stream);
            String xml = new String(content);
            return xml;
        } catch (IOException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't get content of " + url, e);
            }
            return "";
        } finally {
            Closeables.closeQuietly(stream);
        }
    }

}
