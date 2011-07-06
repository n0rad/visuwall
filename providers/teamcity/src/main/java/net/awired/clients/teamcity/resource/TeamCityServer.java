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

package net.awired.clients.teamcity.resource;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "server")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityServer {

    @XmlAttribute
    private Integer versionMinor;

    @XmlAttribute
    private Integer versionMajor;

    @XmlAttribute
    private String version;

    @XmlAttribute
    private String startTime;

    @XmlAttribute
    private String currentTime;

    @XmlAttribute
    private String buildNumber;

    public Integer getVersionMinor() {
        return versionMinor;
    }

    public void setVersionMinor(Integer versionMinor) {
        this.versionMinor = versionMinor;
    }

    public Integer getVersionMajor() {
        return versionMajor;
    }

    public void setVersionMajor(Integer versionMajor) {
        this.versionMajor = versionMajor;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public Date getStartTime() {
        return DateAdapter.parseDate(startTime);
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public Date getCurrentTime() {
        return DateAdapter.parseDate(currentTime);
    }

    public void setCurrentTime(String currentTime) {
        this.currentTime = currentTime;
    }

    public String getBuildNumber() {
        return buildNumber;
    }

    public void setBuildNumber(String buildNumber) {
        this.buildNumber = buildNumber;
    }

}
