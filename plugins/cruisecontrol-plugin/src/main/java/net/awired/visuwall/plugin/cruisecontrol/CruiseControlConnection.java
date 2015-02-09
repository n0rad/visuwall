package fr.norad.visuwall.plugin.cruisecontrol;

import java.util.Date;
import java.util.List;
import java.util.Map;

import fr.norad.visuwall.api.domain.BuildState;
import fr.norad.visuwall.api.domain.BuildTime;
import fr.norad.visuwall.api.domain.Commiter;
import fr.norad.visuwall.api.domain.ProjectKey;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.exception.BuildIdNotFoundException;
import fr.norad.visuwall.api.exception.BuildNotFoundException;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.MavenIdNotFoundException;
import fr.norad.visuwall.api.exception.ProjectNotFoundException;
import fr.norad.visuwall.api.plugin.capability.BuildCapability;

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
