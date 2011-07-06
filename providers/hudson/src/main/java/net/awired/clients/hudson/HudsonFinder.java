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

package net.awired.clients.hudson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.hudson.domain.HudsonBuild;
import net.awired.clients.hudson.domain.HudsonCommiter;
import net.awired.clients.hudson.domain.HudsonJob;
import net.awired.clients.hudson.domain.HudsonTestResult;
import net.awired.clients.hudson.exception.HudsonBuildNotFoundException;
import net.awired.clients.hudson.exception.HudsonJobNotFoundException;
import net.awired.clients.hudson.exception.HudsonViewNotFoundException;
import net.awired.clients.hudson.helper.HudsonXmlHelper;
import net.awired.clients.hudson.helper.MavenHelper;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonUser;
import net.awired.visuwall.hudsonclient.generated.hudson.HudsonView;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelHudson;
import net.awired.visuwall.hudsonclient.generated.hudson.hudsonmodel.HudsonModelView;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonMavenMavenModuleSet;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelBallColor;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelJob;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmoduleset.HudsonModelRun;
import net.awired.visuwall.hudsonclient.generated.hudson.mavenmodulesetbuild.HudsonMavenMavenModuleSetBuild;
import net.awired.visuwall.hudsonclient.generated.hudson.surefireaggregatedreport.HudsonMavenReportersSurefireAggregatedReport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import com.google.common.annotations.VisibleForTesting;

class HudsonFinder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonFinder.class);

    private HudsonUrlBuilder hudsonUrlBuilder;

    @VisibleForTesting
    GenericSoftwareClient client;

    @VisibleForTesting
    HudsonBuildBuilder hudsonBuildBuilder;

    @VisibleForTesting
    TestResultBuilder testResultBuilder;

    HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder) {
        this.client = new GenericSoftwareClient();
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
        this.testResultBuilder = new TestResultBuilder();
    }

    HudsonBuild find(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        HudsonMavenMavenModuleSetBuild setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        String[] commiterNames = HudsonXmlHelper.getCommiterNames(setBuild);
        Set<HudsonCommiter> commiters = findCommiters(commiterNames);
        HudsonBuild hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, commiters);
        return hudsonBuild;
    }

    HudsonMavenReportersSurefireAggregatedReport findSurefireReport(String jobName,
            HudsonMavenMavenModuleSetBuild setBuild) {
        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(jobName, setBuild.getNumber());
        try {
            return client.resource(testResultUrl, HudsonMavenReportersSurefireAggregatedReport.class);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    private HudsonMavenMavenModuleSetBuild findBuildByJobNameAndBuildNumber(String jobName, int buildNumber)
            throws HudsonBuildNotFoundException {
        try {
            String buildUrl = hudsonUrlBuilder.getBuildUrl(jobName, buildNumber);
            HudsonMavenMavenModuleSetBuild setBuild = client.resource(buildUrl, HudsonMavenMavenModuleSetBuild.class);
            return setBuild;
        } catch (ResourceNotFoundException e) {
            throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName, e);
        }
    }

    List<String> findJobNames() {
        List<String> jobNames = new ArrayList<String>();
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        try {
            HudsonModelHudson hudson = client.resource(projectsUrl, HudsonModelHudson.class);
            for (Object job : hudson.getJob()) {
                Node element = (Node) job;
                String name = getJobName(element);
                jobNames.add(name);
            }
        } catch (ResourceNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return jobNames;
    }

    List<String> findJobNamesByView(String viewName) throws HudsonViewNotFoundException {
        try {
            List<String> jobNames = new ArrayList<String>();
            String viewUrl = hudsonUrlBuilder.getViewUrl(viewName);
            HudsonView view = client.resource(viewUrl, HudsonView.class);
            for (HudsonModelJob job : view.getJobs()) {
                jobNames.add(job.getName());
            }
            return jobNames;
        } catch (ResourceNotFoundException e) {
            throw new HudsonViewNotFoundException(e.getMessage(), e);
        }
    }

    List<String> findViews() {
        List<String> views = new ArrayList<String>();
        try {
            String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
            HudsonModelHudson hudson = client.resource(projectsUrl, HudsonModelHudson.class);
            for (HudsonModelView view : hudson.getView()) {
                addValidViewName(views, view);
            }
        } catch (ResourceNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return views;
    }

    private void addValidViewName(List<String> views, HudsonModelView view) {
        String viewName = view.getName();
        if (!"All".equals(viewName) && !"Tous".equals(viewName)) {
            views.add(viewName);
        }
    }

    String getDescription(String jobName) throws HudsonJobNotFoundException {
        HudsonMavenMavenModuleSet moduleSet = findJobByName(jobName);
        return moduleSet.getDescription();
    }

    HudsonJob findJob(String projectName) throws HudsonJobNotFoundException {
        HudsonMavenMavenModuleSet moduleSet = findJobByName(projectName);
        return createHudsonProjectFrom(moduleSet);
    }

    int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        HudsonMavenMavenModuleSet job = findJobByName(projectName);
        HudsonModelRun run = job.getLastBuild();
        if (run == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return run.getNumber();
    }

    boolean isBuilding(String projectName) throws HudsonJobNotFoundException {
        HudsonModelJob job = findJobByName(projectName);
        return HudsonXmlHelper.getIsBuilding(job);
    }

    Set<HudsonCommiter> findCommiters(String[] commiterNames) {
        Set<HudsonCommiter> commiters = new TreeSet<HudsonCommiter>();
        for (String commiterName : commiterNames) {
            try {
                String url = hudsonUrlBuilder.getUserUrl(commiterName);
                HudsonUser hudsonUser = client.resource(url, HudsonUser.class);
                HudsonCommiter commiter = new HudsonCommiter(hudsonUser.getId());
                commiter.setName(commiterName);
                commiter.setEmail(hudsonUser.getEmail());
                commiters.add(commiter);
            } catch (ResourceNotFoundException e) {
                if (LOG.isDebugEnabled()) {
                    LOG.debug("Can't find user " + commiterName, e);
                }
            }
        }
        return commiters;
    }

    String getStateOf(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        HudsonMavenMavenModuleSetBuild build = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        return HudsonXmlHelper.getState(build);
    }

    private HudsonMavenMavenModuleSet findJobByName(String jobName) throws HudsonJobNotFoundException {
        try {
            String jobUrl = hudsonUrlBuilder.getJobUrl(jobName);
            if (MavenHelper.isNotMavenProject(jobUrl)) {
                LOG.warn(jobName + " is not a maven project");
                throw new HudsonJobNotFoundException(jobName + " is not a maven project");
            }
            HudsonMavenMavenModuleSet moduleSet = client.resource(jobUrl, HudsonMavenMavenModuleSet.class);
            return moduleSet;
        } catch (ResourceNotFoundException e) {
            throw new HudsonJobNotFoundException(e);
        }
    }

    private String getJobName(Node node) {
        Node firstChild = node.getFirstChild();
        Node firstChild2 = firstChild.getFirstChild();
        String projectName = firstChild2.getNodeValue();
        return projectName;
    }

    private HudsonJob createHudsonProjectFrom(HudsonMavenMavenModuleSet moduleSet) {
        String name = moduleSet.getName();
        String description = moduleSet.getDescription();
        HudsonModelBallColor color = moduleSet.getColor();
        boolean disabled = color == HudsonModelBallColor.DISABLED || color == HudsonModelBallColor.GREY;

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(name);
        hudsonJob.setDescription(description);
        hudsonJob.setDisabled(disabled);
        return hudsonJob;
    }

    List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        HudsonModelJob modelJob = findJobByName(jobName);
        List<HudsonModelRun> builds = modelJob.getBuild();
        List<Integer> buildNumbers = new ArrayList<Integer>();
        for (HudsonModelRun build : builds) {
            int buildNumber = build.getNumber();
            buildNumbers.add(buildNumber);
        }
        Collections.sort(buildNumbers);
        return buildNumbers;
    }

    HudsonBuild getCompletedBuild(String jobName) throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        HudsonModelJob modelJob = findJobByName(jobName);

        boolean isBuilding = getIsBuilding(modelJob);
        HudsonModelRun lastCompletedHudsonRun;
        if (isBuilding) {
            lastCompletedHudsonRun = modelJob.getLastCompletedBuild();
        } else {
            lastCompletedHudsonRun = modelJob.getLastBuild();
        }
        int lastCompleteBuildNumber = -1;
        if (lastCompletedHudsonRun != null) {
            lastCompleteBuildNumber = lastCompletedHudsonRun.getNumber();
        }
        HudsonBuild lastCompletedHudsonBuild = null;
        if (lastCompleteBuildNumber != -1) {
            lastCompletedHudsonBuild = find(jobName, lastCompleteBuildNumber);
        }
        return lastCompletedHudsonBuild;
    }

    HudsonBuild getCurrentBuild(String jobName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        HudsonModelJob modelJob = findJobByName(jobName);
        HudsonModelRun currentHudsonRun = modelJob.getLastBuild();
        int currentBuildNumber = -1;
        if (currentHudsonRun != null) {
            currentBuildNumber = currentHudsonRun.getNumber();
        }
        HudsonBuild currentHudsonBuild = null;
        if (currentBuildNumber != -1) {
            currentHudsonBuild = find(jobName, currentBuildNumber);
        }
        return currentHudsonBuild;
    }

    private boolean getIsBuilding(HudsonModelJob modelJob) {
        String color = modelJob.getColor().value();
        return color.endsWith("_anime");
    }

    public HudsonTestResult findUnitTestResult(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        HudsonMavenMavenModuleSetBuild setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        HudsonMavenReportersSurefireAggregatedReport surefireReport = findSurefireReport(jobName, setBuild);
        if (surefireReport != null) {
            return testResultBuilder.buildUnitTestResult(surefireReport);
        }
        return new HudsonTestResult();
    }

    public HudsonTestResult findIntegrationTestResult(String jobName, int buildNumber)
            throws HudsonBuildNotFoundException {
        HudsonMavenMavenModuleSetBuild setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        HudsonMavenReportersSurefireAggregatedReport surefireReport = findSurefireReport(jobName, setBuild);
        if (surefireReport != null) {
            return testResultBuilder.buildIntegrationTestResult(surefireReport);
        }
        return new HudsonTestResult();
    }

}
