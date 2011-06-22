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

import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

public interface BasicCapability {

    /**
     * If plugin can find maven id, it should return it or throw MavenIdNotFoundException
     * 
     * @param projectId
     * @return
     */
    String getMavenId(SoftwareProjectId projectId) throws ProjectNotFoundException, MavenIdNotFoundException;

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
     * Return the description of the project
     * 
     * @param projectId
     * @return
     */
    String getDescription(SoftwareProjectId projectId) throws ProjectNotFoundException;

    /**
     * Return the name of the project
     * 
     * @param projectId
     * @return
     */
    String getName(SoftwareProjectId projectId) throws ProjectNotFoundException;

    /**
     * @param projectKey
     * @return
     */
    SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException;

    /**
     * Return the full list of project id contained in the software
     * 
     * @return
     */
    List<SoftwareProjectId> findAllSoftwareProjectIds();

    /**
     * Return a list of project id contained in the software by a list of names
     * 
     * @return
     */
    List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names);

    /**
     * Find all project names of projects handle by the software
     * 
     * @return
     */
    List<String> findProjectNames();

}
