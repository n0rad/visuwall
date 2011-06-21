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

package net.awired.visuwall.api.plugin.capability;

import java.util.List;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ViewNotFoundException;

public interface ViewCapability extends BasicCapability {

    /**
     * Return a list of project id contained in the software by view names
     * 
     * @return
     */
    @Deprecated
    List<ProjectId> findProjectIdsByViews(List<String> views);

    /**
     * Return a list of project id contained in the software by view names
     * 
     * @return
     */
    List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views);

    /**
     * Software can sort projects by views, or graphically by tabs. If so, plugin can list these views.
     * 
     * @return List of view names
     */
    List<String> findViews();

    /**
     * If software sorts its projects by view, you should be able to retrieve project names by view name
     * 
     * @param viewName
     * @return List of project names contained in view
     * @throws ViewNotFoundException
     */
    List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException;

}
