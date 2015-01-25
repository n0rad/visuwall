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
package fr.norad.visuwall.providers.hudson.resource;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;
import javax.xml.bind.annotation.XmlRootElement;
import com.google.common.base.Objects;

@XmlRootElement(name = "hudson")
@XmlAccessorType(XmlAccessType.FIELD)
public class Hudson {

    private String assignedLabel;

    private String mode;

    private String nodeDescription;

    private String nodeName;

    private int numExecutors;

    private String overallLoad;

    private PrimaryView primaryView;

    private int slaveAgentPort;

    private boolean useCrumbs;

    private boolean useSecurity;

    @XmlElements({ @XmlElement(name = "job") })
    private List<Job> jobs = new ArrayList<Job>();

    @XmlElements({ @XmlElement(name = "view") })
    private List<View> views = new ArrayList<View>();

    public String getAssignedLabel() {
        return assignedLabel;
    }

    public void setAssignedLabel(String assignedLabel) {
        this.assignedLabel = assignedLabel;
    }

    public String getMode() {
        return mode;
    }

    public void setMode(String mode) {
        this.mode = mode;
    }

    public String getNodeDescription() {
        return nodeDescription;
    }

    public void setNodeDescription(String nodeDescription) {
        this.nodeDescription = nodeDescription;
    }

    public String getNodeName() {
        return nodeName;
    }

    public void setNodeName(String nodeName) {
        this.nodeName = nodeName;
    }

    public int getNumExecutors() {
        return numExecutors;
    }

    public void setNumExecutors(int numExecutors) {
        this.numExecutors = numExecutors;
    }

    public String getOverallLoad() {
        return overallLoad;
    }

    public void setOverallLoad(String overallLoad) {
        this.overallLoad = overallLoad;
    }

    public PrimaryView getPrimaryView() {
        return primaryView;
    }

    public void setPrimaryView(PrimaryView primaryView) {
        this.primaryView = primaryView;
    }

    public int getSlaveAgentPort() {
        return slaveAgentPort;
    }

    public void setSlaveAgentPort(int slaveAgentPort) {
        this.slaveAgentPort = slaveAgentPort;
    }

    public boolean isUseCrumbs() {
        return useCrumbs;
    }

    public void setUseCrumbs(boolean useCrumbs) {
        this.useCrumbs = useCrumbs;
    }

    public boolean isUseSecurity() {
        return useSecurity;
    }

    public void setUseSecurity(boolean useSecurity) {
        this.useSecurity = useSecurity;
    }

    public List<Job> getJobs() {
        return jobs;
    }

    public void setJobs(List<Job> jobs) {
        this.jobs = jobs;
    }

    public List<View> getViews() {
        return views;
    }

    public void setViews(List<View> views) {
        this.views = views;
    }

    @Override
    public String toString() {
        return Objects.toStringHelper(this) //
                .add("assignedLabel", assignedLabel) //
                .add("mode", mode) //
                .add("nodeDescription", nodeDescription) //
                .add("nodeName", nodeName) //
                .add("numExecutors", numExecutors) //
                .add("overallLoad", overallLoad) //
                .add("slaveAgentPort", slaveAgentPort) //
                .add("useCrumbs", useCrumbs) //
                .add("useSecurity", useSecurity) //
                .toString();
    }

}
