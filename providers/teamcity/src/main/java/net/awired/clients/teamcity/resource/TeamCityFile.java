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

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="file")
@XmlAccessorType(XmlAccessType.FIELD)
public class TeamCityFile {

    @XmlAttribute(name="relative-file")
    private String relativeFile;
    
    @XmlAttribute
    private String file;
    
    @XmlAttribute(name="after-revision")
    private String afterRevision;
    
    @XmlAttribute(name="before-revision")
    private String beforeRevision;

    public String getRelativeFile() {
        return relativeFile;
    }

    public void setRelativeFile(String relativeFile) {
        this.relativeFile = relativeFile;
    }

    public String getFile() {
        return file;
    }

    public void setFile(String file) {
        this.file = file;
    }

    public String getAfterRevision() {
        return afterRevision;
    }

    public void setAfterRevision(String afterRevision) {
        this.afterRevision = afterRevision;
    }

    public String getBeforeRevision() {
        return beforeRevision;
    }

    public void setBeforeRevision(String beforeRevision) {
        this.beforeRevision = beforeRevision;
    }

}
