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
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.plugin.capability.BasicCapability;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;

public class Project implements Comparable<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);

    private final String id = new BigInteger(42, new SecureRandom()).toString(36);
    private String name;
    private String description;

    private List<Integer> buildNumbers;
    protected Map<Integer, Build> builds = new HashMap<Integer, Build>();

    private int lastBuildNumber;
    private int lastNotBuildingNumber;
    private int previousCompletedBuildNumber;

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

    public Project(SoftwareProjectId projectId, BuildCapability buildCapability) {
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
            LOG.debug("Build with id " + lastBuildNumber + " not found and will be created for project " + this);
            build = new Build(buildNumber);
            this.builds.put(buildNumber, build);
        }
        return build;
    }

    @JsonIgnore
    public Build getLastBuild() throws BuildNotFoundException {
        Build lastBuild = builds.get(lastBuildNumber);
        if (lastBuild == null) {
            throw new BuildNotFoundException("No last build found for project " + this);
        }
        return lastBuild;
    }

    @Override
    public String toString() {
        //TODO check new info
        ToStringHelper toString = Objects.toStringHelper(this) //
                .add("id", id) //
                .add("name", name) //
                .add("buildProjectId", buildProjectId) //
                .add("qualityResult", qualityResult); //
        return toString.toString();
    }

    @Override
    public int compareTo(Project project) {
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
        Project other = (Project) obj;
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

    public String getId() {
        return id;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setLastBuildNumber(int lastBuildNumber) {
        this.lastBuildNumber = lastBuildNumber;
    }

    public int getLastBuildNumber() {
        return lastBuildNumber;
    }

    public Map<Integer, Build> getBuilds() {
        return builds;
    }

    public void setBuilds(Map<Integer, Build> builds) {
        this.builds = builds;
    }

    public void setBuildNumbers(List<Integer> buildNumbers) {
        this.buildNumbers = buildNumbers;
    }

    public List<Integer> getBuildNumbers() {
        return buildNumbers;
    }

    public void setLastNotBuildingNumber(int lastNotBuildingNumber) {
        this.lastNotBuildingNumber = lastNotBuildingNumber;
    }

    public int getLastNotBuildingNumber() {
        return lastNotBuildingNumber;
    }

    public void setPreviousCompletedBuildNumber(int previousCompletedBuildNumber) {
        this.previousCompletedBuildNumber = previousCompletedBuildNumber;
    }

    public int getPreviousCompletedBuildNumber() {
        return previousCompletedBuildNumber;
    }

}
