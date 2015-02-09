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

package fr.norad.visuwall.providers.teamcity.resource;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityProject {

    @XmlAttribute
    private String name;

    @XmlAttribute
    private String id;

    @XmlAttribute
    private String href;

    @XmlAttribute
    private String webUrl;

    @XmlAttribute
    private String description;

    @XmlAttribute
    private boolean archived;

    @XmlElementWrapper(name = "buildTypes")
    @XmlElements(value = { @XmlElement(name = "buildType") })
    private List<TeamCityBuildType> buildTypes = new ArrayList<TeamCityBuildType>();

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getHref() {
        return href;
    }

    public void setHref(String href) {
        this.href = href;
    }

    public String getWebUrl() {
        return webUrl;
    }

    public void setWebUrl(String webUrl) {
        this.webUrl = webUrl;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isArchived() {
        return archived;
    }

    public void setArchived(boolean archived) {
        this.archived = archived;
    }

    public List<TeamCityBuildType> getBuildTypes() {
        return buildTypes;
    }

    public void setBuildTypes(List<TeamCityBuildType> buildTypes) {
        this.buildTypes = buildTypes;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof TeamCityProject && id != null) {
            TeamCityProject tcp = (TeamCityProject) obj;
            return id.equals(tcp.id);
        }
        return false;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(id, name, href);
    }

}
