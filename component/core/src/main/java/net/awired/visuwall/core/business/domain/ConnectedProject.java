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

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;

public class ConnectedProject extends Project {

    //TODO move to build
    private State state;

    @Transient
    private Map<SoftwareProjectId, BasicCapability> capabilities = new HashMap<SoftwareProjectId, BasicCapability>();
    @Transient
    private final BuildCapability buildConnection;
    @Transient
    private final SoftwareProjectId buildProjectId;

    //    private ProjectKey projectKey;

    @Transient
    private ScheduledFuture<Object> updateProjectTask;

    public ConnectedProject(SoftwareProjectId projectId, BuildCapability buildCapability) {
        super(projectId.getProjectId());
        this.buildConnection = buildCapability;
        this.buildProjectId = projectId;
        this.capabilities.put(getBuildProjectId(), buildCapability);
    }

    public void close() {
        updateProjectTask.cancel(true);
    }

    public Build findCreatedBuild(Integer buildNumber) {
        Build build = builds.get(buildNumber);
        if (build == null) {
            build = new Build();
            this.builds.put(buildNumber, build);
        }
        return build;
    }

    /////////////////////////////////

    @JsonIgnore
    public BuildCapability getBuildConnection() {
        return buildConnection;
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

    public SoftwareProjectId getBuildProjectId() {
        return buildProjectId;
    }

}
