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
package fr.norad.visuwall.plugin.cruisecontrol;

import java.util.Date;
import java.util.List;
import java.util.Map;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.ConnectionException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.plugin.capability.BuildCapability;

public class CruiseControlConnection implements BuildCapability {

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        // TODO Auto-generated method stub

    }

    @Override
    public void close() {
        // TODO Auto-generated method stub

    }

    @Override
    public boolean isClosed() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        // TODO Auto-generated method stub
        return null;
    }

}
