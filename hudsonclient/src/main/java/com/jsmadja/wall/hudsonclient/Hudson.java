/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.jsmadja.wall.hudsonclient;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.xerces.dom.ElementNSImpl;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;

import com.jsmadja.wall.hudsonclient.builder.HudsonUrlBuilder;
import com.jsmadja.wall.hudsonclient.builder.TestResultBuilder;
import com.jsmadja.wall.hudsonclient.domain.HudsonBuild;
import com.jsmadja.wall.hudsonclient.domain.HudsonProject;
import com.jsmadja.wall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import com.jsmadja.wall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import com.jsmadja.wall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;
import com.jsmadja.wall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelRun;
import com.jsmadja.wall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import com.jsmadja.wall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonModelUser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class Hudson {

    private static final Logger LOG = LoggerFactory.getLogger(Hudson.class);

    private HudsonUrlBuilder hudsonUrlBuilder;
    private TestResultBuilder hudsonTestService = new TestResultBuilder();

    private Client client;

    public Hudson(String hudsonUrl) {
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses();
        client = buildJerseyClient(clientConfig);

        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url " + hudsonUrl);
        }
    }

    /**
     * @return List of all available projects on Hudson
     */
    public final List<HudsonProject> findAllProjects() {
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        if (LOG.isDebugEnabled()) {
            LOG.debug("All project url : " + projectsUrl);
        }
        WebResource hudsonResource = client.resource(projectsUrl);
        HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
        List<HudsonProject> projects = new ArrayList<HudsonProject>();
        List<Object> hudsonProjects = hudson.getJob();
        for (Object project : hudsonProjects) {
            ElementNSImpl element = (ElementNSImpl) project;
            String name = getProjectName(element);
            try {
                HudsonProject hudsonProject = findProject(name);
                projects.add(hudsonProject);
            } catch (HudsonProjectNotFoundException e) {
                LOG.warn(
                        "Can't find project with name ["
                        + name
                        + "] but should be because the list comes from Hudson itself",
                        e);
            }
        }
        return projects;
    }

    /**
     * @param projectName
     * @param buildNumber
     * @return HudsonBuild found in Hudson with its project name and build
     *         number
     */
    public final HudsonBuild findBuild(String projectName, int buildNumber) {
        if (LOG.isDebugEnabled()) {
            LOG.debug("Find build with project name [" + projectName
                    + "] and buildNumber [" + buildNumber + "]");
        }

        String buildUrl = hudsonUrlBuilder.getJobUrl(projectName, buildNumber);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Build url : " + buildUrl);
        }
        WebResource jobResource = client.resource(buildUrl);
        HudsonMavenMavenModuleSetBuild setBuild = jobResource
        .get(HudsonMavenMavenModuleSetBuild.class);

        HudsonBuild hudsonBuild = new HudsonBuild();
        hudsonBuild.setDuration(setBuild.getDuration());
        hudsonBuild.setStartTime(new Date(setBuild.getTimestamp()));
        hudsonBuild.setSuccessful(isSuccessful(setBuild));
        hudsonBuild.setCommiters(getCommiters(setBuild));

        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(projectName,
                buildNumber);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Test result : " + testResultUrl);
        }
        WebResource testResultResource = client.resource(testResultUrl);
        hudsonBuild.setTestResult(hudsonTestService.build(testResultResource));

        return hudsonBuild;
    }

    /**
     * @param projectName
     * @return HudsonProject found with its name
     * @throws HudsonProjectNotFoundException
     */
    public final HudsonProject findProject(String projectName)
    throws HudsonProjectNotFoundException {
        String projectUrl = hudsonUrlBuilder.getProjectUrl(projectName);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Project url : " + projectUrl);
        }
        WebResource projectResource = client.resource(projectUrl);
        try {
            return createHudsonProject(projectResource);
        } catch (HudsonProjectNotCreatedException e) {
            throw new HudsonProjectNotFoundException(e);
        }
    }

    /**
     * @param projectName
     * @return Average build duration time computed with old successful jobs
     * @throws HudsonProjectNotFoundException
     */
    public final long getAverageBuildDurationTime(String projectName)
    throws HudsonProjectNotFoundException {
        HudsonProject hudsonProject = findProject(projectName);
        float sumBuildDurationTime = 0;

        int[] successfulBuilds = getSuccessfulBuildNumbers(hudsonProject);
        for (int buildNumber : successfulBuilds) {
            HudsonBuild successfulBuild = findBuild(projectName, buildNumber);
            sumBuildDurationTime += successfulBuild.getDuration();
        }
        long averageTime = (long) (sumBuildDurationTime / successfulBuilds.length);
        if (LOG.isDebugEnabled()) {
            LOG.debug("Average build time of " + projectName + " is "
                    + averageTime + " ms");
        }
        return averageTime;
    }

    private String[] getCommiters(HudsonMavenMavenModuleSetBuild setBuild) {
        List<HudsonModelUser> users = setBuild.getCulprit();
        String[] commiters = new String[users.size()];
        for (int i = 0; i < users.size(); i++) {
            commiters[i] = users.get(i).getFullName();
        }
        return commiters;
    }

    private boolean getIsBuilding(HudsonModelJob modelJob) {
        String color = modelJob.getColor().value();
        return color.endsWith("_anime");
    }

    private HudsonProject createHudsonProject(WebResource projectResource)
    throws HudsonProjectNotCreatedException {
        try {
            HudsonMavenMavenModuleSet modelJob = projectResource
            .get(HudsonMavenMavenModuleSet.class);

            HudsonProject hudsonProject = new HudsonProject();
            hudsonProject.setName(modelJob.getName());
            hudsonProject.setDescription(modelJob.getDescription());
            if (modelJob.getLastBuild() != null) {
                hudsonProject.setLastBuildNumber(modelJob.getLastBuild()
                        .getNumber());
            }
            hudsonProject.setBuilding(getIsBuilding(modelJob));
            if (!modelJob.getModule().isEmpty()) {
                hudsonProject.setArtifactId(modelJob.getModule().get(0)
                        .getName());
            }
            hudsonProject.setBuildNumbers(getBuildNumbers(modelJob));
            hudsonProject.setLastBuild(findBuild(hudsonProject.getName(),
                    hudsonProject.getLastBuildNumber()));

            return hudsonProject;
        } catch (UniformInterfaceException e) {
            LOG.error(
                    "Error when calling createHudsonProject with projectResource:"
                    + projectResource, e);
            throw new HudsonProjectNotCreatedException(e);
        }
    }

    /**
     * @param hudsonProject
     * @return An array of successful build numbers
     */
    public final int[] getSuccessfulBuildNumbers(HudsonProject hudsonProject) {
        List<Integer> successfulBuildNumbers = new ArrayList<Integer>();
        for (Integer buildNumber : hudsonProject.getBuildNumbers()) {
            HudsonBuild build = findBuild(hudsonProject.getName(), buildNumber);
            if (build.isSuccessful()) {
                successfulBuildNumbers.add(buildNumber);
            }
        }
        int[] successfulBuilds = new int[successfulBuildNumbers.size()];
        for (int i = 0; i < successfulBuildNumbers.size(); i++) {
            successfulBuilds[i] = successfulBuildNumbers.get(i);
        }
        return successfulBuilds;
    }

    private int[] getBuildNumbers(HudsonModelJob modelJob) {
        List<HudsonModelRun> builds = modelJob.getBuild();
        int[] buildNumbers = new int[builds.size()];
        for (int i = 0; i < builds.size(); i++) {
            buildNumbers[i] = builds.get(i).getNumber();
        }
        return buildNumbers;
    }

    private boolean isSuccessful(HudsonMavenMavenModuleSetBuild job) {
        ElementNSImpl element = (ElementNSImpl) job.getResult();

        if (element == null) {
            return false;
        }

        Node result = element.getFirstChild();
        if (result == null) {
            return false;
        }

        return "SUCCESS".equals(result.getNodeValue());
    }

    private String getProjectName(ElementNSImpl element) {
        return element.getFirstChild().getFirstChild().getNodeValue();
    }

    /**
     * @param projectName
     * @return Date which we think the project will finish to build
     * @throws HudsonProjectNotFoundException
     */
    public final Date getEstimatedFinishTime(String projectName)
    throws HudsonProjectNotFoundException {
        HudsonProject project = findProject(projectName);
        HudsonBuild lastBuild = project.getLastBuild();
        Date startTime = lastBuild.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(projectName);
        DateTime estimatedFinishTime = new DateTime(startTime.getTime())
        .plus(averageBuildDurationTime);

        if (LOG.isDebugEnabled()) {
            LOG.debug("Estimated finish time of project " + projectName + " is "
                    + estimatedFinishTime + " ms");
        }

        return estimatedFinishTime.toDate();
    }

    Client buildJerseyClient(ClientConfig clientConfig) {
        return Client.create(clientConfig);
    }

}