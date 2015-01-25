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
package fr.norad.visuwall.providers.hudson;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import fr.norad.visuwall.providers.common.GenericSoftwareClient;
import fr.norad.visuwall.providers.common.ResourceNotFoundException;
import fr.norad.visuwall.providers.hudson.domain.HudsonBuild;
import fr.norad.visuwall.providers.hudson.domain.HudsonCommiter;
import fr.norad.visuwall.providers.hudson.domain.HudsonJob;
import fr.norad.visuwall.providers.hudson.domain.HudsonTestResult;
import fr.norad.visuwall.providers.hudson.exception.HudsonBuildNotFoundException;
import fr.norad.visuwall.providers.hudson.exception.HudsonJobNotFoundException;
import fr.norad.visuwall.providers.hudson.exception.HudsonViewNotFoundException;
import fr.norad.visuwall.providers.hudson.helper.HudsonXmlHelper;
import fr.norad.visuwall.providers.hudson.resource.Build;
import fr.norad.visuwall.providers.hudson.resource.Color;
import fr.norad.visuwall.providers.hudson.resource.ExternalJob;
import fr.norad.visuwall.providers.hudson.resource.FreeStyleBuild;
import fr.norad.visuwall.providers.hudson.resource.FreeStyleProject;
import fr.norad.visuwall.providers.hudson.resource.Hudson;
import fr.norad.visuwall.providers.hudson.resource.HudsonUser;
import fr.norad.visuwall.providers.hudson.resource.Job;
import fr.norad.visuwall.providers.hudson.resource.ListView;
import fr.norad.visuwall.providers.hudson.resource.MatrixProject;
import fr.norad.visuwall.providers.hudson.resource.MavenModuleSet;
import fr.norad.visuwall.providers.hudson.resource.MavenModuleSetBuild;
import fr.norad.visuwall.providers.hudson.resource.Project;
import fr.norad.visuwall.providers.hudson.resource.SurefireAggregatedReport;
import fr.norad.visuwall.providers.hudson.resource.View;

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

    public HudsonFinder(HudsonUrlBuilder hudsonUrlBuilder, String login, String password) {
        this.client = new GenericSoftwareClient(login, password);
        this.hudsonUrlBuilder = hudsonUrlBuilder;
        this.hudsonBuildBuilder = new HudsonBuildBuilder();
        this.testResultBuilder = new TestResultBuilder();
    }

    HudsonBuild find(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        Build setBuild = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        String[] commiterNames = HudsonXmlHelper.getCommiterNames(setBuild);
        Set<HudsonCommiter> commiters = findCommiters(commiterNames);
        HudsonBuild hudsonBuild = hudsonBuildBuilder.createHudsonBuild(setBuild, commiters);
        return hudsonBuild;
    }

    SurefireAggregatedReport findSurefireReport(String jobName, Build build) {
        String testResultUrl = hudsonUrlBuilder.getTestResultUrl(jobName, build.getNumber());
        try {
            return client.resource(testResultUrl, SurefireAggregatedReport.class);
        } catch (ResourceNotFoundException e) {
            return null;
        }
    }

    @VisibleForTesting
    Build findBuildByJobNameAndBuildNumber(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        String buildUrl = hudsonUrlBuilder.getBuildUrl(jobName, buildNumber);
        if (LOG.isDebugEnabled()) {
            LOG.debug(buildUrl);
        }
        Build setBuild = null;
        try {
            setBuild = client.resource(buildUrl, MavenModuleSetBuild.class);
        } catch (ResourceNotFoundException e) {
            try {
                setBuild = client.resource(buildUrl, FreeStyleBuild.class);
            } catch (ResourceNotFoundException e1) {
                throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName, e1);
            }
        }

        if (setBuild == null) {
            throw new HudsonBuildNotFoundException("Build #" + buildNumber + " not found for job " + jobName);
        }
        return setBuild;
    }

    List<String> findJobNames() {
        List<String> jobNames = new ArrayList<String>();
        String projectsUrl = hudsonUrlBuilder.getAllProjectsUrl();
        try {
            Hudson hudson = client.resource(projectsUrl, Hudson.class);
            for (Job job : hudson.getJobs()) {
                String name = job.getName();
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
            ListView view = client.resource(viewUrl, ListView.class);
            for (Job job : view.getJobs()) {
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
            Hudson hudson = client.resource(projectsUrl, Hudson.class);
            for (View view : hudson.getViews()) {
                views.add(view.getName());
            }
        } catch (ResourceNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
        }
        return views;
    }

    String getDescription(String jobName) throws HudsonJobNotFoundException {
        Project moduleSet = findJobByName(jobName);
        return moduleSet.getDescription();
    }

    HudsonJob findJob(String projectName) throws HudsonJobNotFoundException {
        Project moduleSet = findJobByName(projectName);
        return createHudsonProjectFrom(moduleSet);
    }

    int getLastBuildNumber(String projectName) throws HudsonJobNotFoundException, HudsonBuildNotFoundException {
        Project job = findJobByName(projectName);
        Build lastBuild = job.getLastBuild();
        if (lastBuild == null) {
            throw new HudsonBuildNotFoundException("Project " + projectName + " has no last build");
        }
        return lastBuild.getNumber();
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
        Build build = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        return build.getResult();
    }

    private Project findJobByName(String jobName) throws HudsonJobNotFoundException {
        String jobUrl = hudsonUrlBuilder.getJobUrl(jobName);
        Project project = findProjectByName(jobName, jobUrl);
        if (project == null) {
            throw new HudsonJobNotFoundException("Can't find job with name '" + jobName + "'");
        }
        return project;
    }

    private Project findProjectByName(String jobName, String jobUrl) throws HudsonJobNotFoundException {
        Project project = null;
        try {
            project = client.resource(jobUrl, MavenModuleSet.class);
        } catch (ResourceNotFoundException e) {
            try {
                project = client.resource(jobUrl, FreeStyleProject.class);
            } catch (ResourceNotFoundException e1) {
                try {
                    project = client.resource(jobUrl, MatrixProject.class);
                } catch (ResourceNotFoundException e2) {
                    try {
                        project = client.resource(jobUrl, ExternalJob.class);
                    } catch (ResourceNotFoundException e3) {
                        throw new HudsonJobNotFoundException("Can't find job with name '" + jobName + "'", e3);
                    }
                }
            }
        }
        return project;
    }

    private HudsonJob createHudsonProjectFrom(Project project) {
        String name = project.getName();
        String description = project.getDescription();
        String color = project.getColor();
        boolean disabled = Color.DISABLED.value().equals(color) || Color.GREY.value().equals(color);

        HudsonJob hudsonJob = new HudsonJob();
        hudsonJob.setName(name);
        hudsonJob.setDescription(description);
        hudsonJob.setDisabled(disabled);
        return hudsonJob;
    }

    List<Integer> getBuildNumbers(String jobName) throws HudsonJobNotFoundException {
        Project modelJob = findJobByName(jobName);
        List<Build> builds = modelJob.getBuilds();
        List<Integer> buildNumbers = new ArrayList<Integer>();
        for (Build build : builds) {
            int buildNumber = build.getNumber();
            buildNumbers.add(buildNumber);
        }
        Collections.sort(buildNumbers);
        return buildNumbers;
    }

    HudsonBuild getCompletedBuild(String jobName) throws HudsonBuildNotFoundException, HudsonJobNotFoundException {
        Project modelJob = findJobByName(jobName);

        boolean isBuilding = getIsBuilding(modelJob);
        Build lastCompletedHudsonRun;
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
        Project modelJob = findJobByName(jobName);
        Build currentHudsonRun = modelJob.getLastBuild();
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

    private boolean getIsBuilding(Project modelJob) {
        String color = modelJob.getColor();
        return color.endsWith("_anime");
    }

    public HudsonTestResult findUnitTestResult(String jobName, int buildNumber) throws HudsonBuildNotFoundException {
        Build build = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        SurefireAggregatedReport surefireReport = findSurefireReport(jobName, build);
        if (surefireReport != null) {
            return testResultBuilder.buildUnitTestResult(surefireReport);
        }
        return new HudsonTestResult();
    }

    public HudsonTestResult findIntegrationTestResult(String jobName, int buildNumber)
            throws HudsonBuildNotFoundException {
        Build build = findBuildByJobNameAndBuildNumber(jobName, buildNumber);
        SurefireAggregatedReport surefireReport = findSurefireReport(jobName, build);
        if (surefireReport != null) {
            return testResultBuilder.buildIntegrationTestResult(surefireReport);
        }
        return new HudsonTestResult();
    }

}
