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

package net.awired.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.awired.clients.bamboo.Bamboo;
import net.awired.clients.bamboo.exception.BambooBuildNotFoundException;
import net.awired.clients.bamboo.exception.BambooBuildNumberNotFoundException;
import net.awired.clients.bamboo.exception.BambooEstimatedFinishTimeNotFoundException;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.exception.BambooStateNotFoundException;
import net.awired.clients.bamboo.resource.Plan;
import net.awired.clients.bamboo.resource.Result;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.State;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.BuildNumberNotFoundException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.plugin.capability.BuildCapability;
import net.awired.visuwall.api.plugin.capability.TestCapability;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class BambooConnection implements BuildCapability, TestCapability {

    @VisibleForTesting
    Bamboo bamboo;

    private boolean connected;

    private static final Logger LOG = LoggerFactory.getLogger(BambooConnection.class);

    @Override
    public void connect(String url, String login, String password) {
        connect(url);
    }

    public void connect(String url) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        bamboo = new Bamboo(url);
        connected = true;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = getProjectKey(softwareProjectId);
            return bamboo.isBuilding(projectName, buildNumber);
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id:" + softwareProjectId, e);
        }
    }

    @Override
    public State getBuildState(SoftwareProjectId projectId, Integer buildNumber) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildNumber(buildNumber);
        try {
            String projectName = getProjectKey(projectId);
            String bambooState = bamboo.getState(projectName);
            return States.asVisuwallState(bambooState);
        } catch (BambooStateNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public int getLastBuildNumber(SoftwareProjectId projectId) throws ProjectNotFoundException,
            BuildNumberNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String id = getProjectKey(projectId);
            return bamboo.getLastResultNumber(id);
        } catch (BambooBuildNumberNotFoundException e) {
            throw new BuildNumberNotFoundException(e);
        }
    }

    @Override
    public List<String> findProjectNames() {
        checkConnected();
        List<String> projectNames = new ArrayList<String>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            projectNames.add(plan.getName());
        }
        return projectNames;
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByNames(List<String> names) {
        checkConnected();
        Preconditions.checkNotNull(names, "names is mandatory");
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String name = plan.getName();
            if (names.contains(name)) {
                SoftwareProjectId projectId = new SoftwareProjectId(plan.getKey());
                projectIds.add(projectId);
            }
        }
        return projectIds;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        return "";
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        checkConnected();
        Preconditions.checkNotNull(projectKey, "projectKey is mandatory");
        String name = projectKey.getName();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String planName = plan.getName();
            if (name.equals(planName)) {
                SoftwareProjectId softwareProjectId = new SoftwareProjectId(plan.getKey());
                return softwareProjectId;
            }
        }
        throw new ProjectNotFoundException("Can't identify project with projectKey:" + projectKey);
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, Integer buildNumber)
            throws ProjectNotFoundException, BuildNotFoundException {
        checkConnected();
        checkBuildNumber(buildNumber);
        String projectName = getProjectKey(projectId);
        try {
            return bamboo.getEstimatedFinishTime(projectName);
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException(e);
        } catch (BambooEstimatedFinishTimeNotFoundException e) {
            return new Date();
        }
    }

    private String getProjectKey(SoftwareProjectId projectId) {
        return projectId.getProjectId();
    }

    private void checkConnected() {
        Preconditions.checkState(connected, "You must connect your plugin");
    }

    @Override
    public List<SoftwareProjectId> findAllSoftwareProjectIds() {
        checkConnected();
        List<SoftwareProjectId> projectIds = new ArrayList<SoftwareProjectId>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String key = plan.getKey();
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
            projectIds.add(softwareProjectId);
        }
        return projectIds;
    }

    @Override
    public Map<String, SoftwareProjectId> listSoftwareProjectIds() {
        checkConnected();
        Map<String, SoftwareProjectId> projects = new HashMap<String, SoftwareProjectId>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String key = plan.getKey();
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
            projects.put(plan.getName(), softwareProjectId);
        }
        return projects;
    }

    @Override
    public List<Integer> getBuildNumbers(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String planKey = softwareProjectId.getProjectId();
        int lastResultNumber;
        try {
            lastResultNumber = bamboo.getLastResultNumber(planKey);
        } catch (BambooBuildNumberNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't find builds numbers of software project id : " + softwareProjectId, e);
            }
            return new ArrayList<Integer>();
        }
        return Arrays.asList(lastResultNumber);
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        throw new MavenIdNotFoundException("Not implemented!");
    }

    @Override
    public String getName(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        try {
            String projectKey = softwareProjectId.getProjectId();
            Plan plan = bamboo.findPlan(projectKey);
            String name = plan.getProjectName();
            return name;
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find name of software project id: " + softwareProjectId);
        }
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        try {
            String projectKey = softwareProjectId.getProjectId();
            Result bambooResult = bamboo.findResult(projectKey, buildNumber);
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(bambooResult.getBuildDuration());
            buildTime.setStartTime(bambooResult.getBuildStartedTime());
            return buildTime;
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildNumber + " of project " + softwareProjectId,
                    e);
        }
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String planKey = softwareProjectId.getProjectId();
        try {
            Plan plan = bamboo.findPlan(planKey);
            return !plan.isEnabled();
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find plan with software project id: " + softwareProjectId, e);
        }
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, Integer buildNumber)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildNumber(buildNumber);
        return new ArrayList<Commiter>();
    }

    private void checkBuildNumber(Integer buildNumber) {
        Preconditions.checkNotNull(buildNumber, "buildNumber is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult result = new TestResult();
        try {
            String planKey = projectId.getProjectId();
            int buildNumber = bamboo.getLastResultNumber(planKey);
            Result findResult = bamboo.findResult(planKey, buildNumber);
            int successfulTestCount = findResult.getSuccessfulTestCount();
            int failedTestCount = findResult.getFailedTestCount();
            result.setFailCount(failedTestCount);
            result.setPassCount(successfulTestCount);
        } catch (BambooBuildNumberNotFoundException e) {
            LOG.warn("Can't analyze unit tests for projectId:" + projectId, e);
        } catch (BambooBuildNotFoundException e) {
            LOG.warn("Can't analyze unit tests for projectId:" + projectId, e);
        }
        return result;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId projectId) {
        return new TestResult();
    }

}
