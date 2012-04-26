package net.awired.visuwall.plugin.bamboo;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import net.awired.clients.bamboo.Bamboo23Client;
import net.awired.clients.bamboo.resource.Build23;
import net.awired.clients.bamboo.resource.Response;
import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;

import com.google.common.collect.Lists;

public class Bamboo23Connection implements BuildCapability {

    private Bamboo23Client client;
    private boolean connected;

    @Override
    public void connect(String url, String login, String password) {
        client = new Bamboo23Client(url, login, password);
        connected = true;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
        Response response = client.getBuildNames();
        for (Build23 build : response.getBuilds()) {
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(build.getKey());
            softwareProjectIds.put(softwareProjectId, build.getName());
        }
        return softwareProjectIds;
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        Response response = client.getBuildNames();
        for (Build23 build : response.getBuilds()) {
            if (build.getKey().equals(projectId.getProjectId())) {
                return build.getName().substring(0, build.getName().lastIndexOf('-')).trim();
            }
        }
        throw new ProjectNotFoundException("Cannot find project with projectId: " + projectId);
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        List<Build23> builds = getBuilds(softwareProjectId);
        return Integer.toString(builds.get(0).getBuildNumber());
    }

    private List<Build23> getBuilds(SoftwareProjectId softwareProjectId) {
        String projectKey = asProjectKey(softwareProjectId);
        List<Build23> builds = client.getRecentlyCompletedBuildResultsForProject(projectKey).getBuilds();
        return builds;
    }

    private String asProjectKey(SoftwareProjectId softwareProjectId) {
        String projectKey = softwareProjectId.getProjectId();
        projectKey = projectKey.substring(0, projectKey.lastIndexOf("-"));
        return projectKey;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        Build23 build = getBuildByNumber(projectId, buildId);
        String state = build.getBuildState();
        return asVisuwallState(state);
    }

    private BuildState asVisuwallState(String state) {
        if (state.equals("Successful")) {
            return BuildState.SUCCESS;
        }
        if (state.equals("Failed")) {
            return BuildState.UNSTABLE;
        }
        return BuildState.UNKNOWN;
    }

    private Build23 getBuildByNumber(SoftwareProjectId projectId, String buildId) throws BuildNotFoundException {
        List<Build23> builds = getBuilds(projectId);
        for (Build23 build : builds) {
            if (build.getBuildNumber().toString().equals(buildId)) {
                return build;
            }
        }
        throw new BuildNotFoundException("Cannot find build '" + buildId + "' for projectId " + projectId);
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        Build23 build = getBuildByNumber(softwareProjectId, buildId);
        BuildTime buildTime = new BuildTime();
        buildTime.setStartTime(build.getBuildTime());
        buildTime.setDuration(build.getBuildDurationInSeconds() * 1000);
        return buildTime;
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        throw new MavenIdNotFoundException("Not implemented.");
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        List<String> buildIds = Lists.newArrayList();
        List<Build23> builds = client.getRecentlyCompletedBuildResultsForProject(asProjectKey(softwareProjectId))
                .getBuilds();
        for (Build23 build : builds) {
            buildIds.add(0, build.getBuildNumber().toString());
        }
        return buildIds;
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        throw new ProjectNotFoundException("Not implemented.");
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        return Lists.newArrayList();
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return new Date();
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        return false;
    }

}
