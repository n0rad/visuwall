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

import java.util.Date;
import java.util.List;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;

public interface BuildCapability extends BasicCapability {

    /**
     * Return a list of commiters
     * 
     * @param softwareProjectId
     * @param buildId
     * @return
     * @throws BuildNotFoundException
     * @throws ProjectNotFoundException
     */
    List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException;

    /**
     * Return build time information
     * 
     * @param softwareProjectId
     * @param buildId
     * @return
     * @throws ProjectNotFoundException
     */
    BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException;

    /**
     * Returns the build ids order by integer ASC
     * Pending builds not included.
     * 
     * @param softwareProjectId
     * @return
     * @throws ProjectNotFoundException
     */
    List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException;

    /**
     * Builds are in a certain state which may vary between software You'll have to try to associate them with common
     * States
     * 
     * @param projectId
     * @param buildId
     * @return
     * @throws ProjectNotFoundException
     * @throws BuildNotFoundException
     */
    BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException;

    /**
     * If a project is building, plugin can calculate the estimated finish time
     * 
     * @param projectId
     * @param buildId
     * @return
     * @throws ProjectNotFoundException
     * @throws BuildNotFoundException
     */
    Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException;

    /**
     * Return true if project is building
     * 
     * @param projectId
     * @param buildId
     * @return
     * @throws ProjectNotFoundException
     * @throws BuildNotFoundException
     */
    boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException;

    /**
     * Return the last build id of a project
     * Pending builds not included.
     * 
     * @param softwareProjectId
     * @return buildId
     * @throws ProjectNotFoundException
     * @throws BuildIdNotFoundException
     */
    String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException;

}
