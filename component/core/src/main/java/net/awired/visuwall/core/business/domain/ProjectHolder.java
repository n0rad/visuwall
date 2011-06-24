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
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.base.Preconditions;

public class ProjectHolder extends ArrayList<Project> {

    private static final Logger LOG = LoggerFactory.getLogger(ProjectHolder.class);

    private static final long serialVersionUID = 1L;

    public List<SoftwareProjectId> getBuildProjectIds() {
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>(this.size());
        for (Project project : this) {
            projectIds.add(project.getBuildProjectId());
        }
        return projectIds;
    }

    public Project getById(String id) throws ProjectNotFoundException {
        Preconditions.checkNotNull(id, "projectId is mandatory");
        for (Project project : this) {
            if (id.equals(project.getId())) {
                return project;
            }
        }
        throw new ProjectNotFoundException("Project not found for this id : " + id);
    }

    public void deleteAndCleanProject(String id) {
        try {
            Project project = this.getById(id);
            project.close();
            this.remove(project);
        } catch (ProjectNotFoundException e) {
            LOG.warn("Project with id " + id + " not found to be remove");
        }
    }
}
