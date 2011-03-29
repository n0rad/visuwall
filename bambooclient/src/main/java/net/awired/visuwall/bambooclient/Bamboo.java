package net.awired.visuwall.bambooclient;

import java.util.ArrayList;
import java.util.List;

import net.awired.visuwall.bambooclient.builder.BambooUrlBuilder;
import net.awired.visuwall.bambooclient.domain.BambooProject;
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

        BambooProject project = createProjectFrom(results.results.get(0).result);
        return project;
    }

}
