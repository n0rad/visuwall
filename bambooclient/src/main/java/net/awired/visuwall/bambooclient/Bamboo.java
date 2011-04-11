package net.awired.visuwall.bambooclient;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.bambooclient.builder.BambooUrlBuilder;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.rest.Build;
import net.awired.visuwall.bambooclient.rest.Builds;
import net.awired.visuwall.bambooclient.rest.Plan;
import net.awired.visuwall.bambooclient.rest.Plans;
import net.awired.visuwall.bambooclient.rest.Result;
import net.awired.visuwall.bambooclient.rest.Results;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;

public final class Bamboo {

    private String bambooUrl;

    private Client client;

    private BambooUrlBuilder bambooUrlBuilder;

    private static final Logger LOG = LoggerFactory.getLogger(Bamboo.class);

    public Bamboo(String bambooUrl) {
        this.bambooUrl = bambooUrl;
        client = Client.create();
        bambooUrlBuilder = new BambooUrlBuilder(bambooUrl);

        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize bamboo with url " + bambooUrl);
        }
    }

    public List<BambooProject> findAllProjects() {
        String projectsUrl = bambooUrlBuilder.getAllProjectsUrl();
        if (LOG.isDebugEnabled()) {
            LOG.debug("All project url : " + projectsUrl);
        }
        WebResource bambooResource = client.resource(projectsUrl);
        Plans plans = bambooResource.get(Plans.class);

        List<BambooProject> projects = new ArrayList<BambooProject>();
        projects.addAll(createProjectsFrom(plans));

        return projects;
    }

    private List<BambooProject> createProjectsFrom(Plans plans) {
        Preconditions.checkNotNull(plans, "plans");

        List<BambooProject> projects = new ArrayList<BambooProject>();
        if (plans.plan != null) {
            for(Plan plan:plans.plan) {
                projects.add(createProjectFrom(plan));
            }
        }
        if (plans.plans != null) {
            projects.addAll(createProjectsFrom(plans.plans));
        }
        return projects;
    }

    private BambooProject createProjectFrom(Plan plan) {
        BambooProject project = new BambooProject();
        project.setName(plan.name);
        project.setKey(plan.key);
        project.setLink(plan.link.href);
        return project;
    }

    private BambooProject createProjectFrom(Result result) {
        BambooProject project = new BambooProject();
        return project;
    }

    public BambooProject findProject(String projectName) {
        Preconditions.checkNotNull(projectName, "projectName");

        String projectsUrl = bambooUrlBuilder.getProjectsUrl(projectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Project url : " + projectsUrl);
        }
        WebResource bambooResource = client.resource(projectsUrl);
        Results results = bambooResource.get(Results.class);

        BambooProject project = createProjectFrom(results.results.get(0).result.get(0));
        String isBuildingUrl = bambooUrlBuilder.getIsBuildingUrl(projectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("IsBuilding url : " + isBuildingUrl);
        }
        bambooResource = client.resource(isBuildingUrl);
        Plan plan = bambooResource.get(Plan.class);
        project.setBuilding(plan.isBuilding);
        return project;
    }

    public int getLastBuildNumber(String projectName) {
        Preconditions.checkNotNull(projectName, "projectName");

        String lastBuildUrl = bambooUrlBuilder.getLatestBuildResult(projectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Last build url : " + lastBuildUrl);
        }
        WebResource bambooResource = client.resource(lastBuildUrl);
        Results results = bambooResource.get(Results.class);
        return results.results.get(0).result.get(0).number;
    }

    public BambooBuild findBuild(String projectName, int buildNumber) {
        Preconditions.checkNotNull(projectName, "projectName");

        String buildUrl = bambooUrlBuilder.getBuildUrl(projectName, buildNumber);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Build url : " + buildUrl);
        }
        WebResource bambooResource = client.resource(buildUrl);
        Result result = bambooResource.get(Result.class);
        BambooBuild build = createBuildFrom(result);
        return build;
    }

    private BambooBuild createBuildFrom(Result result) {
        BambooBuild bambooBuild = new BambooBuild();
        bambooBuild.setBuildNumber(result.number);
        bambooBuild.setDuration(result.buildDuration.longValue());
        bambooBuild.setStartTime(result.buildStartedTime);
        bambooBuild.setState(result.state);
        bambooBuild.setPassCount(result.successfulTestCount);
        bambooBuild.setFailCount(result.failedTestCount);
        return bambooBuild;
    }

    public String getState(String projectName) {
        Preconditions.checkNotNull(projectName, "projectName");

        String lastBuildUrl = bambooUrlBuilder.getLastBuildUrl();
        if (LOG.isDebugEnabled()) {
            LOG.debug("Last build url : " + lastBuildUrl);
        }

        int lastBuildNumber = getLastBuildNumber(projectName);
        String key = projectName+"-"+lastBuildNumber;

        WebResource bambooResource = client.resource(lastBuildUrl);
        Builds builds = bambooResource.get(Builds.class);
        List<Build> allBuilds = builds.builds.get(0).build;
        for (Build build:allBuilds) {
            if (key.equals(build.key)) {
                return build.state;
            }
        }
        throw new RuntimeException("Not state found for projectName: "+projectName);
    }

}
