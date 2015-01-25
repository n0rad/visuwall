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

import com.google.common.base.Objects;

public class SoftwareProjectId {

    private String projectId;

    public SoftwareProjectId(String projectId) {
        this.projectId = projectId;
    }

    @Override
    public String toString() {
        return projectId;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(projectId);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof SoftwareProjectId) {
            SoftwareProjectId p = (SoftwareProjectId) obj;
            return Objects.equal(projectId, p.projectId);
        }
        return false;
    }

    //////////////////////////////////////////////////////////////

    public void setProjectId(String projectId) {
        this.projectId = projectId;
    }

    public String getProjectId() {
        return projectId;
    }
}
