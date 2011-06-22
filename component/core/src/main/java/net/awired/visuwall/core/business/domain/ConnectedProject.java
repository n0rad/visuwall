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

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;

public class ConnectedProject implements Comparable<ConnectedProject> {

    private final String id = new BigInteger(42, new SecureRandom()).toString(36);

    private String description;
    private int[] buildNumbers;
    protected Map<Integer, Build> builds = new HashMap<Integer, Build>();

    private int completedBuildId;
    private int currentBuildId;
    //    private ProjectKey projectKey;

    private QualityResult qualityResult = new QualityResult();

    @Transient
    private Map<SoftwareProjectId, BasicCapability> capabilities = new HashMap<SoftwareProjectId, BasicCapability>();
    @Transient
    private final BuildCapability buildConnection;
    @Transient
    private final SoftwareProjectId buildProjectId;
    @Transient
    private ScheduledFuture<Object> updateProjectTask;

    /////////////////////////////////////////////////////////////////////////////////////

    public ConnectedProject(SoftwareProjectId projectId, BuildCapability buildCapability) {
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");
        Preconditions.checkNotNull(buildCapability, "buildCapability is a mandatory parameter");
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

    @JsonIgnore
    public Build getCompletedBuild() {
        return builds.get(completedBuildId);
    }

    @JsonIgnore
    public void setCompletedBuild(Build completedBuild) {
        // TODO remove completedBuild;
        //        this.builds.put(completedBuild.getBuildNumber(), completedBuild);
        //        this.completedBuildId = completedBuild.getBuildNumber();
    }

    @JsonIgnore
    public Build getCurrentBuild() throws BuildNotFoundException {
        Build build = builds.get(getCurrentBuildId());
        if (build == null) {
            throw new BuildNotFoundException("Build not found in project");
        }
        return build;
    }

    @JsonIgnore
    public void setCurrentBuild(Build currentBuild) {
        this.builds.put(currentBuild.getBuildNumber(), currentBuild);
        this.setCurrentBuildId(currentBuild.getBuildNumber());
    }

    @Override
    public String toString() {
        //TODO check new info
        ToStringHelper toString = Objects.toStringHelper(this) //
                .add("id", id) //
                .add("buildProjectId", buildProjectId) //
                .add("qualityResult", qualityResult); //
        return toString.toString();
    }

    @Override
    public int compareTo(ConnectedProject project) {
        Preconditions.checkNotNull(project, "project");
        return getId().compareTo(project.getId());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? 0 : id.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        ConnectedProject other = (ConnectedProject) obj;
        if (id == null) {
            if (other.id != null) {
                return false;
            }
        } else if (!id.equals(other.id)) {
            return false;
        }
        return true;
    }

    /////////////////////////////////

    @JsonIgnore
    public BuildCapability getBuildConnection() {
        return buildConnection;
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

    public QualityResult getQualityResult() {
        return qualityResult;
    }

    public int[] getBuildNumbers() {
        return buildNumbers;
    }

    public void setBuildNumbers(int[] buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    public int getCompletedBuildId() {
        return completedBuildId;
    }

    public int getCurrentBuildId() {
        return currentBuildId;
    }

    public String getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setCurrentBuildId(int currentBuildId) {
        this.currentBuildId = currentBuildId;
    }

}
