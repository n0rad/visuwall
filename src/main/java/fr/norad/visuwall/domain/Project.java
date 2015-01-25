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
package fr.norad.visuwall.domain;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import javax.persistence.Transient;
import org.codehaus.jackson.annotate.JsonIgnore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.plugin.capability.BasicCapability;
import fr.norad.visuwall.plugin.capability.BuildCapability;

public class Project implements Comparable<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(Project.class);

    private final String id = new BigInteger(42, new SecureRandom()).toString(36);
    private String name;
    private String description;
    private boolean disabled;

    private List<String> buildIds;
    protected Map<String, Build> builds = new HashMap<String, Build>();

    private String lastBuildId;
    private String lastNotBuildingId;
    private String previousCompletedBuildId;
    private Date lastUpdate;

    @Transient
    private ProjectKey projectKey;
    @Transient
    private Map<SoftwareProjectId, BasicCapability> capabilities = new HashMap<SoftwareProjectId, BasicCapability>();
    @Transient
    private final BuildCapability buildConnection;
    @Transient
    private final SoftwareProjectId buildProjectId;
    @Transient
    private ScheduledFuture<?> updateProjectTask;

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

    public Build findCreatedBuild(String buildId) {
        Build build = builds.get(buildId);
        if (build == null) {
            LOG.debug("Build with id " + lastBuildId + " not found and will be created for project " + this);
            build = new Build(buildId);
            this.builds.put(buildId, build);
        }
        return build;
    }

    @JsonIgnore
    public Build getLastBuild() throws BuildNotFoundException {
        Build lastBuild = builds.get(lastBuildId);
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
                .add("buildProjectId", buildProjectId);
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
    public ScheduledFuture<?> getUpdateProjectTask() {
        return updateProjectTask;
    }

    @JsonIgnore
    public void setUpdateProjectTask(ScheduledFuture<?> updateProjectTask) {
        this.updateProjectTask = updateProjectTask;
    }

    @JsonIgnore
    public SoftwareProjectId getBuildProjectId() {
        return buildProjectId;
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

    public void setLastBuildId(String lastBuildId) {
        this.lastBuildId = lastBuildId;
    }

    public String getLastBuildId() {
        return lastBuildId;
    }

    public Map<String, Build> getBuilds() {
        return builds;
    }

    public void setBuilds(Map<String, Build> builds) {
        this.builds = builds;
    }

    public void setBuildId(List<String> buildId) {
        this.buildIds = buildId;
    }

    public List<String> getBuildId() {
        return buildIds;
    }

    public void setLastNotBuildingId(String lastNotBuildingId) {
        this.lastNotBuildingId = lastNotBuildingId;
    }

    public String getLastNotBuildingId() {
        return lastNotBuildingId;
    }

    public void setPreviousCompletedBuildId(String previousCompletedBuildId) {
        this.previousCompletedBuildId = previousCompletedBuildId;
    }

    public String getPreviousCompletedBuildId() {
        return previousCompletedBuildId;
    }

    @JsonIgnore
    public Map<SoftwareProjectId, BasicCapability> getCapabilities() {
        return capabilities;
    }

    @JsonIgnore
    public void setCapabilities(Map<SoftwareProjectId, BasicCapability> capabilities) {
        this.capabilities = capabilities;
    }

    @JsonIgnore
    public ProjectKey getProjectKey() {
        return projectKey;
    }

    @JsonIgnore
    public void setProjectKey(ProjectKey projectKey) {
        this.projectKey = projectKey;
    }

    public void setLastUpdate(Date lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public Date getLastUpdate() {
        return lastUpdate;
    }

    public void setDisabled(boolean disabled) {
        this.disabled = disabled;
    }

    public boolean isDisabled() {
        return disabled;
    }

}
