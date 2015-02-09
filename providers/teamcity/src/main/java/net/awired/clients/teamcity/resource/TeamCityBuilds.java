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
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "builds")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityBuilds {

    @XmlAttribute
    private String nextHref;

    @XmlAttribute
    private int count;

    @XmlElements({ @XmlElement(name = "build") })
    private List<TeamCityBuildItem> builds = new ArrayList<TeamCityBuildItem>();

    public String getNextHref() {
        return nextHref;
    }

    public void setNextHref(String nextHref) {
        this.nextHref = nextHref;
    }

    public int getCount() {
        return count;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public List<TeamCityBuildItem> getBuilds() {
        return builds;
    }

    public void setBuilds(List<TeamCityBuildItem> builds) {
        this.builds = builds;
    }

}
