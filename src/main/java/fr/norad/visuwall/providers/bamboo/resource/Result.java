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
package fr.norad.visuwall.providers.bamboo.resource;

import java.util.Date;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import lombok.Data;

@Data
@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement(name = "result")
public class Result {

    @XmlAttribute
    private int id;

    @XmlAttribute
    private int number;

    @XmlAttribute
    private String lifeCycleState;

    @XmlAttribute
    private String state;

    @XmlAttribute
    private String key;

    private Link link;

    private Date buildStartedTime;

    private Date buildCompletedTime;

    private int buildDurationInSeconds;

    private int buildDuration;

    private String buildDurationDescription;

    private String buildRelativeTime;

    private int vcsRevisionKey;

    private String buildTestSummary;

    private int successfulTestCount;

    private int failedTestCount;

    private String buildReason;

}
