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
package fr.norad.visuwall.api.plugin.capability;

import java.util.Map;
import fr.norad.visuwall.api.domain.ProjectKey;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.MavenIdNotFoundException;
import fr.norad.visuwall.api.exception.ProjectNotFoundException;

public interface BasicCapability {

    /**
     * If plugin can find maven id, it should return it or throw MavenIdNotFoundException
     * 
     * @param softwareProjectId
     * @return
     */
    String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException, MavenIdNotFoundException;

    /**
     * Initiate connection to the software
     * 
     * @param url
     * @param login
     * @param password
     * @throws ConnectionException
     */
    void connect(String url, String login, String password) throws ConnectionException;

    /**
     * Close the connection to the software
     */
    void close();

    /**
     * Return the state of the connection
     */
    boolean isClosed();

    /**
     * Return the description of the project
     * 
     * @param softwareProjectId
     * @return
     */
    String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException;

    /**
     * Return the name of the project
     * 
     * @param projectId
     * @return
     */
    String getName(SoftwareProjectId projectId) throws ProjectNotFoundException;

    /**
     * Find the software project id with informations contained in project key
     * 
     * @param projectKey
     * @return
     */
    SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException;

    /**
     * Return the full list of project id contained in the software with there display name
     * 
     * @return
     */
    Map<SoftwareProjectId, String> listSoftwareProjectIds();

    /**
     * Returns true if project is disabled in the software
     * 
     * @param projectId
     * @return
     * @throws ProjectNotFoundException
     */
    boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException;
}
