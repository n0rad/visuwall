/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.bambooclient.rest;

import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;


@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class Result {

    @XmlAttribute
    public Integer id;

    @XmlAttribute
    public Integer number;

    @XmlAttribute
    public String lifeCycleState;

    @XmlAttribute
    public String state;

    @XmlAttribute
    public String key;

    public Link link;

    public Date buildStartedTime;

    public Date buildCompletedTime;

    public Integer buildDurationInSeconds;

    public Integer buildDuration;

    public String buildDurationDescription;

    public String buildRelativeTime;

    public Integer vcsRevisionKey;

    public String buildTestSummary;

    public Integer successfulTestCount;

    public Integer failedTestCount;

    public String buildReason;
}
