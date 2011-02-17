package com.jsmadja.wall.projectwall;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.jsmadja.wall.projectwall.generated.hudson.HudsonMavenMavenModuleSet;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonMavenMavenModuleSetBuild;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelHudson;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelJob;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelRun;
import com.jsmadja.wall.projectwall.generated.hudson.HudsonModelUser;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.org.apache.xerces.internal.dom.ElementNSImpl;

public class HudsonService {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonService.class);

    private String hudsonUrl;

    private static final String API_XML = "/api/xml";
    private static final String ALL_JOBS_URI = "";
    private static final String JOB_URI = "/job";

    private Client client = new Client();

    public HudsonService(String hudsonUrl) {
        this.hudsonUrl = hudsonUrl;

        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize hudson with url "+hudsonUrl);
        }

    }

    public List<HudsonJob> findAllJobs() {
        String jobsUrl = hudsonUrl+ALL_JOBS_URI+API_XML;
        if (LOG.isInfoEnabled()) {
            LOG.info("Access to "+jobsUrl);
        }
        WebResource hudsonResource = client.resource(jobsUrl);
        HudsonModelHudson hudson = hudsonResource.get(HudsonModelHudson.class);
        List<HudsonJob> jobs = new ArrayList<HudsonJob>();
        List<Object> _jobs = hudson.getJob();
        for(Object _job:_jobs) {
            ElementNSImpl element = (ElementNSImpl) _job;
            String name = getJobName(element);
            HudsonJob hudsonJob = findJob(name);
            jobs.add(hudsonJob);
        }
        return jobs;
    }

    public HudsonJob findJob(String jobName, int buildNumber) {
        if (LOG.isInfoEnabled()) {
            LOG.info("Find job name ["+jobName+"] buildNumber ["+buildNumber+"]");
        }
        HudsonJob hudsonJob = findJob(jobName);
        addBuildInfoTo(hudsonJob, buildNumber);
        return hudsonJob;
    }

    public HudsonJob findJob(String jobName) {
        WebResource  jobResource = client.resource(hudsonUrl+JOB_URI+"/"+jobName+API_XML);
        HudsonJob hudsonJob = createHudsonJob(jobResource);
        return hudsonJob;
    }

    public long getAverageBuildDurationTime(String jobName) {
        HudsonJob hudsonJob = findJob(jobName);
        float sumBuildDurationTime = 0;

        int[] successfulBuilds = getSuccessfulBuildNumbers(hudsonJob, hudsonJob.getBuildNumbers());
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
        WebResource jobResource = client.resource(hudsonUrl+JOB_URI+"/"+hudsonJob.getName()+"/"+buildNumber+API_XML);
        HudsonMavenMavenModuleSetBuild setBuild = jobResource.get(HudsonMavenMavenModuleSetBuild.class);
        hudsonJob.setDuration(setBuild.getDuration());
        hudsonJob.setStartTime(new Date(setBuild.getTimestamp()));

        String[] commiters = getCommiters(setBuild);
        hudsonJob.setCommiters(commiters);
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
        boolean successful = getJobStatus(modelJob);
        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(modelJob.getName());
        hudsonJob.setSuccessful(successful);
        hudsonJob.setDescription(modelJob.getDescription());
        hudsonJob.setLastBuildNumber(modelJob.getLastBuild().getNumber());
        hudsonJob.setBuilding(getIsBuilding(modelJob));
        hudsonJob.setArtifactId(modelJob.getModule().get(0).getName());

        int[] buildNumbers = getBuildNumbers(modelJob);
        hudsonJob.setBuildNumbers(buildNumbers);

        return hudsonJob;
    }

    private int[] getSuccessfulBuildNumbers(HudsonJob hudsonJob, int[] buildNumbers) {
        List<Integer> successfulBuildNumbers = new ArrayList<Integer>();
        for (Integer buildNumber:buildNumbers) {
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

    private boolean getJobStatus(HudsonModelJob job) {
        HudsonModelRun lastBuild = job.getLastBuild();
        if (lastBuild == null) {
            return false;
        }
        int lastBuildNumber = lastBuild.getNumber();
        int lastSuccessfulBuildNumber = job.getLastSuccessfulBuild().getNumber();
        return lastBuildNumber == lastSuccessfulBuildNumber;
    }

    private String getJobName(ElementNSImpl element) {
        return element.getFirstChild().getFirstChild().getNodeValue();
    }

    public long getEstimatedRemainingTime(String jobName) {
        HudsonJob job = findJob(jobName);

        if (!job.isBuilding()) {
            return 0;
        }

        int lastBuildNumber = job.getLastBuildNumber();
        HudsonJob lastJob = findJob(jobName, lastBuildNumber);
        Date startTime = lastJob.getStartTime();
        long averageBuildDurationTime = getAverageBuildDurationTime(jobName);
        DateTime remainingTime = new DateTime().minus(startTime.getTime()).minus(averageBuildDurationTime);

        if (LOG.isInfoEnabled()) {
            LOG.info("Estimated remaining time for "+jobName+" is "+remainingTime.getMillis()+" ms");
        }

        return remainingTime.getMillis();
    }

}
