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

import java.util.Date;
import net.awired.visuwall.api.domain.Build;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface BuildCapability extends BasicCapability {

    //    int[] getBuildNumbers(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Builds are in a certain state which may vary between software You'll have to try to associate them with common
     * States
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    State getLastBuildState(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Build software can order their builds by number, plugin should be able to retrieve builds by number too
     * 
     * @param projectId
     * @param buildNumber
     * @return
     * @throws BuildNotFoundException
     * @throws ProjectNotFoundException
     * @deprecated
     */
    @Deprecated
    Build findBuildByBuildNumber(ProjectId projectId, int buildNumber) throws BuildNotFoundException,
            ProjectNotFoundException;

    /**
     * If a project is building, plugin can calculate the estimated finish time
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    Date getEstimatedFinishTime(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return true if project is building
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    boolean isBuilding(ProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return the last build number of a project
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     * @throws BuildNumberNotFoundException
     */
    int getLastBuildNumber(ProjectId projectId) throws ProjectNotFoundException, BuildNumberNotFoundException;

}
