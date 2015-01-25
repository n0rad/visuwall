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
package fr.norad.visuwall.providers.pivotal.resource;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name = "project")
@XmlAccessorType(XmlAccessType.FIELD)
public class Project {

    private Integer id;

    private String name;

    @XmlElement(name = "iteration_length")
    private Integer iterationLength;

    @XmlElement(name = "last_activity_at")
    private String lastActivityAt;

    @XmlElement(name = "week_start_day")
    private String weekStartDay;

    @XmlElement(name = "first_iteration_start_time")
    private String firstIterationStartTime;

    @XmlElement(name = "current_iteration_number")
    private Integer currentIterationNumber;

    public Integer getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public Date getLastActivityAt() {
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(lastActivityAt);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid format " + lastActivityAt, e);
        }
    }

    public Integer getIterationLength() {
        return iterationLength;
    }

    public String getWeekStartDay() {
        return weekStartDay;
    }

    public void setFirstIterationStartTime(String firstIterationStartTime) {
        this.firstIterationStartTime = firstIterationStartTime;
    }

    public Date getFirstIterationStartTime() {
        try {
            return new SimpleDateFormat("yyyy/MM/dd hh:mm:ss").parse(firstIterationStartTime);
        } catch (ParseException e) {
            throw new IllegalArgumentException("Invalid format " + firstIterationStartTime, e);
        }
    }

    public void setIterationLength(Integer iterationLength) {
        this.iterationLength = iterationLength;
    }

    public void setWeekStartDay(String weekStartDay) {
        this.weekStartDay = weekStartDay;
    }

    public void setLastActivityAt(String lastActivityAt) {
        this.lastActivityAt = lastActivityAt;
    }

    public Integer getCurrentIterationNumber() {
        return currentIterationNumber;
    }

    public void setCurrentIterationNumber(Integer currentIterationNumber) {
        this.currentIterationNumber = currentIterationNumber;
    }

}
