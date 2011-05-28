/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.api.plugin;

import java.util.Date;
import java.util.List;

import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.Project;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.ProjectStatus.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;


public interface BuildConnectionPlugin extends ConnectionPlugin {

    List<ProjectId> findAllProjects();

    Project findProject(ProjectId projectId) throws ProjectNotFoundException;

    Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException, ProjectNotFoundException;

    /**
     * Populating a project means fill its attributes with all data that you can fetch from your system
     * If you can fetch State or Build informations, add it in <code>project</code>!
     * @param project Project to populate
     * @throws ProjectNotFoundException Throw this exception if you can't find this project in your system
     */
    void populate(Project project) throws ProjectNotFoundException;

    Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException;

    boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException;

    State getState(ProjectId projectId) throws ProjectNotFoundException;

    int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNotFoundException;

    List<String> findProjectNames();
}