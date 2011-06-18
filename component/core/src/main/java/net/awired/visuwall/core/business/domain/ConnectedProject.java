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

package net.awired.visuwall.core.business.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ConnectedProject extends Project {

    private boolean building;

    //TODO move to build
    private State state;

    @Transient
    private List<BasicCapability> capabilities = new ArrayList<BasicCapability>();
    @Transient
    private BuildCapability buildConnection;
    @Transient
    private ScheduledFuture<Object> updateProjectTask;

    public void close() {
        updateProjectTask.cancel(true);
    }

    public ConnectedProject(String name) {
        super(name);
    }

    public ConnectedProject(ProjectId projectId) {
        super(projectId);
    }

    /////////////////////////////////

    @JsonIgnore
    public void setCapabilities(List<BasicCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonIgnore
    public List<BasicCapability> getCapabilities() {
        return capabilities;
    }

    @JsonIgnore
    public BuildCapability getBuildConnection() {
        return buildConnection;
    }

    @JsonIgnore
    public void setBuildConnection(BuildCapability buildConnection) {
        this.buildConnection = buildConnection;
    }

    public void setBuilding(boolean building) {
        this.building = building;
    }

    public boolean isBuilding() {
        return building;
    }

    public void setState(State state) {
        this.state = state;
    }

    public State getState() {
        return state;
    }

    @JsonIgnore
    public ScheduledFuture<Object> getUpdateProjectTask() {
        return updateProjectTask;
    }

    @JsonIgnore
    public void setUpdateProjectTask(ScheduledFuture<Object> updateProjectTask) {
        this.updateProjectTask = updateProjectTask;
    }

}
