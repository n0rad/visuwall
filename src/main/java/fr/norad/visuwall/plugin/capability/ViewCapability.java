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
package fr.norad.visuwall.plugin.capability;

import java.util.List;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.exception.ViewNotFoundException;

public interface ViewCapability extends BasicCapability {

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
     * @Deprecated not usable as we do not have id
     */
    List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException;

    //    Map<SoftwareProjectId, String> findJobNamesByView(String viewName) throws ViewNotFoundException;

}
