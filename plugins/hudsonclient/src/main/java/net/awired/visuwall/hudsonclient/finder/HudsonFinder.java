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

package net.awired.visuwall.hudsonclient.finder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import net.awired.visuwall.hudsonclient.builder.HudsonBuildBuilder;
import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;
import net.awired.visuwall.hudsonclient.domain.HudsonBuild;
import net.awired.visuwall.hudsonclient.domain.HudsonCommiter;
import net.awired.visuwall.hudsonclient.domain.HudsonJob;
import net.awired.visuwall.hudsonclient.exception.HudsonBuildNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonJobNotFoundException;
import net.awired.visuwall.hudsonclient.exception.HudsonViewNotFoundException;
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
import net.awired.visuwall.hudsonclient.helper.HudsonXmlHelper;
import net.awired.visuwall.hudsonclient.helper.MavenHelper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Node;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class HudsonFinder {

    private static final Logger LOG = LoggerFactory.getLogger(HudsonFinder.class);

    private HudsonUrlBuilder hudsonUrlBuilder;

    @VisibleForTesting
    GenericSoftwareClient client;

    @VisibleForTesting
    HudsonBuildBuilder hudsonBuildBuilder;

    public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder) {
        this.client = new GenericSoftwareClient();
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
    }

    public HudsonBuild find(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        checkJobName(jobName);
        checkBuildNumber(buildNumber);
        HudsonMavenMavenModuleSetBuild setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        String[] commiterNames = HudsonXmlHelper.getCommiterNames(setBuild);
        Set<HudsonCommiter> commiters = findCommiters(commiterNames);
        HudsonMavenReportersSurefireAggregatedReport surefireReport = findSurefireReport(jobName, setBuild);
        HudsonBuild hudsonBuild;
        if (surefireReport == null) {
            hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, commiters);
        } else {
            hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, surefireReport, commiters);
        }
        return hudsonBuild;
    }

    private HudsonMavenReportersSurefireAggregatedReport findSurefireReport(String jobName,
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
        checkJobName(jobName);
        checkBuildNumber(buildNumber);
        try {
            String buildUrl = hudsonUrlBuilder.getBuildUrl(jobName, buildNumber);
            HudsonMavenMavenModuleSetBuild setBuild = client.resource(buildUrl, HudsonMavenMavenModuleSetBuild.class);
            return setBuild;
        } catch (ResourceNotFoundException e) {
            throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName, e);
        }
    }

    public List<String> findJobNames() {
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

    public List<String> findJobNamesByView(String viewName) throws HudsonViewNotFoundException {
        Preconditions.checkNotNull(viewName, "viewName is mandatory");
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

    public List<String> findViews() {
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

    public String getDescription(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
        HudsonMavenMavenModuleSet moduleSet = findJobByName(jobName);
        return moduleSet.getDescription();
    }

    public HudsonJob findJob(String projectName) throws HudsonJobNotFoundException {
        HudsonMavenMavenModuleSet moduleSet = findJobByName(projectName);
        return createHudsonProjectFrom(moduleSet);
    }

    public int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        checkJobName(projectName);
        HudsonMavenMavenModuleSet job = findJobByName(projectName);
        HudsonModelRun run = job.getLastBuild();
        if (run == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return run.getNumber();
    }

    public boolean isBuilding(String projectName) throws HudsonJobNotFoundException {
        checkJobName(projectName);
        HudsonModelJob job = findJobByName(projectName);
        return HudsonXmlHelper.getIsBuilding(job);
    }

    public Set<HudsonCommiter> findCommiters(String[] commiterNames) {
        Preconditions.checkNotNull(commiterNames, "commiterNames is mandatory");
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

    public String getStateOf(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        checkJobName(jobName);
        checkBuildNumber(buildNumber);
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

    private void checkBuildNumber(int buildNumber) {
        Preconditions.checkArgument(buildNumber >= 0, "buidNumber must be positive");
    }

    private void checkJobName(String jobName) {
        Preconditions.checkNotNull(jobName, "jobName is mandatory");
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
        boolean disabled = moduleSet.getColor() == HudsonModelBallColor.DISABLED;

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(name);
        hudsonJob.setDescription(description);
        hudsonJob.setDisabled(disabled);
        return hudsonJob;
    }

    public List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        checkJobName(jobName);
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

    public HudsonBuild getCompletedBuild(String jobName) throws HudsonBuildNotFoundException,
            HudsonJobNotFoundException {
        checkJobName(jobName);
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

    public HudsonBuild getCurrentBuild(String jobName) throws HudsonJobNotFoundException,
            HudsonBuildNotFoundException {
        checkJobName(jobName);
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

}
