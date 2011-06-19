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

package net.awired.visuwall.api.domain;

import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.HashMap;
import java.util.Map;
import net.awired.visuwall.api.domain.quality.QualityResult;
import org.codehaus.jackson.annotate.JsonIgnore;
import com.google.common.base.Objects;
import com.google.common.base.Objects.ToStringHelper;
import com.google.common.base.Preconditions;

public class Project implements Comparable<Project> {

    private final String id = new BigInteger(42, new SecureRandom()).toString(36);
    private ProjectId projectId;
    private String description;
    private QualityResult qualityResult = new QualityResult();
    private int[] buildNumbers;

    protected Map<Integer, Build> builds = new HashMap<Integer, Build>();

    private int completedBuildId;
    private int currentBuildId;

    public Project(String name) {
        Preconditions.checkNotNull(name, "name is a mandatory parameter");
        projectId = new ProjectId();
        projectId.setName(name);
    }

    public Project(ProjectId projectId) {
        Preconditions.checkNotNull(projectId, "projectId is a mandatory parameter");
        this.projectId = projectId;
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
    public Build getCurrentBuild() {
        return builds.get(getCurrentBuildId());
    }

    @JsonIgnore
    public void setCurrentBuild(Build currentBuild) {
        this.builds.put(currentBuild.getBuildNumber(), currentBuild);
        this.setCurrentBuildId(currentBuild.getBuildNumber());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectId);
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Project)) {
            return false;
        }

        if (projectId == null) {
            return false;
        }

        Project project = (Project) obj;
        return projectId.equals(project.projectId);
    }

    @Override
    public String toString() {
        //TODO check new info
        ToStringHelper toString = Objects.toStringHelper(this) //
                .add("id", id) //
                .add("projectId", projectId) //
                .add("name", getName()) //
                .add("quality result", qualityResult); //
        return toString.toString();
    }

    @Override
    public int compareTo(Project project) {
        Preconditions.checkNotNull(project, "project");
        return getName().compareTo(project.getName());
    }

    public void addId(String idName, String idValue) {
        projectId.addId(idName, idValue);
    }

    // ////////////////////////////////////////////////////////////////

    public String getName() {
        return projectId.getName();
    }

    public void setName(String name) {
        projectId.setName(name);
    }

    public ProjectId getProjectId() {
        return projectId;
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
