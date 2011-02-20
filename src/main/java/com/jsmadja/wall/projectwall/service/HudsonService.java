/**
 * Copyright (C) 2010 Julien SMADJA <julien.smadja@gmail.com> - Arnaud LEMAIRE
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

package com.jsmadja.wall.projectwall.service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsmadja.wall.projectwall.HudsonUrlBuilder;
import com.jsmadja.wall.projectwall.domain.HudsonJob;
import com.jsmadja.wall.projectwall.domain.TestResult;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonMavenMavenModuleSet;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonMavenMavenModuleSetBuild;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelHudson;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelJob;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelRun;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelUser;
import com.jsmadja.wall.projectwall.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.ClientHandlerException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class HudsonService {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonService.class);

    private HudsonUrlBuilder hudsonUrlBuilder;

    private Client client;

    public HudsonService(String hudsonUrl) {
        hudsonUrlBuilder = new HudsonUrlBuilder(hudsonUrl);
        ClientConfig clientConfig = new DefaultClientConfig();
        clientConfig.getClasses();
        client = buildJerseyClient(clientConfig);

        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url "+hudsonUrl);
        }
    }

    /**
     * @return List of all available jobs on Hudson
     */
    public final List<HudsonJob> findAllJobs() {
        String jobsUrl = hudsonUrlBuilder.getAllJobsUrl();
        if (LOG.isInfoEnabled()) {
            LOG.info("All jobs url : "+jobsUrl);
        }
        WebResource hudsonResource = client.resource(jobsUrl);
        HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
        List<HudsonJob> jobs = new ArrayList<HudsonJob>();
        List<Object> hudsonJobs = hudson.getJob();
        for(Object job:hudsonJobs) {
            ElementNSImpl element = (ElementNSImpl) job;
            String name = getJobName(element);
            HudsonJob hudsonJob = findJob(name);
            jobs.add(hudsonJob);
        }
        return jobs;
    }

    /**
     * @param jobName
     * @param buildNumber
     * @return HudsonJob found in Hudson with its name and build number
     */
    public final HudsonJob findJob(String jobName, int buildNumber) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Find job name ["+jobName+"] buildNumber ["+buildNumber+"]");
        }
        HudsonJob hudsonJob = findJob(jobName);
        addBuildInfoTo(hudsonJob, buildNumber);
        return hudsonJob;
    }

    /**
     * @param jobName
     * @return HudsonJob found with its name
     */
    public final HudsonJob findJob(String jobName) {
        String jobUrl = hudsonUrlBuilder.getJobUrl(jobName);
        if (LOG.isInfoEnabled()) {
            LOG.info("Job url : "+jobUrl);
        }
        WebResource  jobResource = client.resource(jobUrl);
        return createHudsonJob(jobResource);
    }

    /**
     * @param jobName
     * @return Average build duration time computed with old successful jobs
     */
    public final long getAverageBuildDurationTime(String jobName) {
        HudsonJob hudsonJob = findJob(jobName);
        float sumBuildDurationTime = 0;

        int[] successfulBuilds = getSuccessfulBuildNumbers(hudsonJob);
        for (int buildNumber:successfulBuilds) {
            HudsonJob successfulJob = findJob(jobName, buildNumber);
            sumBuildDurationTime += successfulJob.getDuration();
        }
        long averageTime = (long) (sumBuildDurationTime/successfulBuilds.length);
        if (LOG.isInfoEnabled()) {
            LOG.info("Average build time of "+jobName+" is "+averageTime+" ms");
        }
        return averageTime;
    }

    private void addBuildInfoTo(HudsonJob hudsonJob, int buildNumber) {
        String jobUrl = hudsonUrlBuilder.getJobUrl(hudsonJob.getName(), buildNumber);
        if (LOG.isInfoEnabled()) {
            LOG.info("Job url : "+jobUrl);
        }
        WebResource jobResource = client.resource(jobUrl);
        HudsonMavenMavenModuleSetBuild setBuild = jobResource.get(HudsonMavenMavenModuleSetBuild.class);
        hudsonJob.setDuration(setBuild.getDuration());
        hudsonJob.setStartTime(new Date(setBuild.getTimestamp()));

        boolean successful = getJobStatus(setBuild);
        hudsonJob.setSuccessful(successful);

        String[] commiters = getCommiters(setBuild);
        hudsonJob.setCommiters(commiters);

        hudsonJob.setTestResult(getTestResult(hudsonJob, buildNumber));
    }

    private String[] getCommiters(HudsonMavenMavenModuleSetBuild setBuild) {
        List<HudsonModelUser> users = setBuild.getCulprit();
        String[] commiters = new String[users.size()];
        for (int i=0; i<users.size(); i++) {
            commiters[i] = users.get(i).getFullName();
        }
        return commiters;
    }

    private boolean getIsBuilding(HudsonModelJob modelJob) {
        String color = modelJob.getColor().value();
        return color.endsWith("_anime");
    }

    private HudsonJob createHudsonJob(WebResource jobResource) {
        HudsonMavenMavenModuleSet modelJob = jobResource.get(HudsonMavenMavenModuleSet.class);
        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(modelJob.getName());
        hudsonJob.setDescription(modelJob.getDescription());
        hudsonJob.setLastBuildNumber(modelJob.getLastBuild().getNumber());
        hudsonJob.setBuilding(getIsBuilding(modelJob));
        hudsonJob.setArtifactId(modelJob.getModule().get(0).getName());

        int[] buildNumbers = getBuildNumbers(modelJob);
        hudsonJob.setBuildNumbers(buildNumbers);

        return hudsonJob;
    }

    /**
     * @param hudsonJob
     * @return An array of successful build numbers
     */
    public final int[] getSuccessfulBuildNumbers(HudsonJob hudsonJob) {
        List<Integer> successfulBuildNumbers = new ArrayList<Integer>();
        for (Integer buildNumber:hudsonJob.getBuildNumbers()) {
            HudsonJob job = findJob(hudsonJob.getName(), buildNumber);
            if(job.isSuccessful()) {
                successfulBuildNumbers.add(buildNumber);
            }
        }
        int[] successfulBuilds = new int[successfulBuildNumbers.size()];
        for (int i=0; i < successfulBuildNumbers.size(); i++) {
            successfulBuilds[i] = successfulBuildNumbers.get(i);
        }
        return successfulBuilds;
    }

    private int[] getBuildNumbers(HudsonModelJob modelJob) {
        List<HudsonModelRun> builds = modelJob.getBuild();
        int[] buildNumbers = new int[builds.size()];
        for (int i=0; i<builds.size(); i++) {
            buildNumbers[i] = builds.get(i).getNumber();
        }
        return buildNumbers;
    }

    private boolean getJobStatus(HudsonMavenMavenModuleSetBuild job) {
        ElementNSImpl element = (ElementNSImpl) job.getResult();
        return "SUCCESS".equals(element.getFirstChild().getNodeValue());
    }

    private String getJobName(ElementNSImpl element) {
        return element.getFirstChild().getFirstChild().getNodeValue();
    }

    /**
     * @param jobName
     * @return Date which we think the job will finish to build
     */
    public Date getEstimatedFinishTime(String jobName) {
        HudsonJob job = findJob(jobName);

        int lastBuildNumber = job.getLastBuildNumber();
        HudsonJob lastJob = findJob(jobName, lastBuildNumber);
        Date startTime = lastJob.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(jobName);
        DateTime estimatedFinishTime = new DateTime(startTime.getTime()).plus(averageBuildDurationTime);

        if (LOG.isInfoEnabled()) {
            LOG.info("Estimated finish time of job "+jobName+" is "+estimatedFinishTime+" ms");
        }

        return estimatedFinishTime.toDate();
    }

    private TestResult getTestResult(HudsonJob hudsonJob, int buildNumber) {
        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(hudsonJob.getName(), buildNumber);
        if (LOG.isInfoEnabled()) {
            LOG.info("Test result : "+testResultUrl);
        }
        WebResource testResultResource = client.resource(testResultUrl);
        TestResult testResult = new TestResult();
        try {
            HudsonMavenReportersSurefireAggregatedReport surefireReport = testResultResource.get(HudsonMavenReportersSurefireAggregatedReport.class);
            testResult.setFailCount(surefireReport.getFailCount());
            testResult.setPassCount(surefireReport.getTotalCount() - surefireReport.getFailCount() - surefireReport.getSkipCount());
            testResult.setSkipCount(surefireReport.getSkipCount());
            testResult.setTotalCount(surefireReport.getTotalCount());
        } catch(ClientHandlerException e) {
            if(LOG.isInfoEnabled()) {
                LOG.info(hudsonJob.getName()+" has no test result");
            }
        }
        return testResult;
    }

    Client buildJerseyClient(ClientConfig clientConfig) {
        return Client.create(clientConfig);
    }

}