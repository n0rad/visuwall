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
package fr.norad.visuwall.plugin.bamboo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;
import fr.norad.visuwall.domain.BuildState;
import fr.norad.visuwall.domain.BuildTime;
import fr.norad.visuwall.domain.Commiter;
import fr.norad.visuwall.domain.ProjectKey;
import fr.norad.visuwall.domain.SoftwareProjectId;
import fr.norad.visuwall.domain.TestResult;
import fr.norad.visuwall.exception.BuildIdNotFoundException;
import fr.norad.visuwall.exception.BuildNotFoundException;
import fr.norad.visuwall.exception.MavenIdNotFoundException;
import fr.norad.visuwall.exception.ProjectNotFoundException;
import fr.norad.visuwall.plugin.capability.BuildCapability;
import fr.norad.visuwall.plugin.capability.TestCapability;
import fr.norad.visuwall.providers.bamboo.Bamboo;
import fr.norad.visuwall.providers.bamboo.exception.BambooBuildNotFoundException;
import fr.norad.visuwall.providers.bamboo.exception.BambooBuildNumberNotFoundException;
import fr.norad.visuwall.providers.bamboo.exception.BambooEstimatedFinishTimeNotFoundException;
import fr.norad.visuwall.providers.bamboo.exception.BambooPlanNotFoundException;
import fr.norad.visuwall.providers.bamboo.exception.BambooStateNotFoundException;
import fr.norad.visuwall.providers.bamboo.resource.Plan;
import fr.norad.visuwall.providers.bamboo.resource.Result;

public class BambooConnection implements BuildCapability, TestCapability {

    @VisibleForTesting
    Bamboo bamboo;

    private boolean connected;

    private static final Logger LOG = LoggerFactory.getLogger(BambooConnection.class);

    @Override
    public void connect(String url, String login, String password) {
        Preconditions.checkNotNull(url, "url is mandatory");
        if (StringUtils.isBlank(url)) {
            throw new IllegalArgumentException("url can't be null.");
        }
        if (StringUtils.isNotBlank(login)) {
            bamboo = new Bamboo(url, login, password);
        } else {
            bamboo = new Bamboo(url);
        }
        connected = true;
    }

    @Override
    public boolean isBuilding(SoftwareProjectId softwareProjectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectName = getProjectKey(softwareProjectId);
            return bamboo.isBuilding(projectName, Integer.valueOf(buildId));
        } catch (BambooPlanNotFoundException e) {
            throw new ProjectNotFoundException("Can't find project with software project id:" + softwareProjectId, e);
        }
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        checkBuildId(buildId);
        try {
            String projectName = getProjectKey(projectId);
            String bambooState = bamboo.getState(projectName);
            return States.asVisuwallState(bambooState);
        } catch (BambooStateNotFoundException e) {
            throw new ProjectNotFoundException(e);
        }
    }

    @Override
    public String getLastBuildId(SoftwareProjectId projectId) throws ProjectNotFoundException, BuildIdNotFoundException {
        checkConnected();
        checkSoftwareProjectId(projectId);
        try {
            String id = getProjectKey(projectId);
            return String.valueOf(bamboo.getLastResultNumber(id));
        } catch (BambooBuildNumberNotFoundException e) {
            throw new BuildIdNotFoundException(e);
        }
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
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        checkConnected();
        checkBuildId(buildId);
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
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        checkConnected();
        Map<SoftwareProjectId, String> projects = new HashMap<SoftwareProjectId, String>();
        List<Plan> plans = bamboo.findAllPlans();
        for (Plan plan : plans) {
            String key = plan.getKey();
            SoftwareProjectId softwareProjectId = new SoftwareProjectId(key);
            projects.put(softwareProjectId, plan.getName());
        }
        return projects;
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        String planKey = softwareProjectId.getProjectId();
        String lastResultNumber;
        try {
            lastResultNumber = String.valueOf(bamboo.getLastResultNumber(planKey));
        } catch (BambooBuildNumberNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug("Can't find builds numbers of software project id : " + softwareProjectId, e);
            }
            return new ArrayList<String>();
        }
        return Arrays.asList(lastResultNumber);
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        checkConnected();
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
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        try {
            String projectKey = softwareProjectId.getProjectId();
            Result bambooResult = bamboo.findResult(projectKey, Integer.valueOf(buildId));
            BuildTime buildTime = new BuildTime();
            buildTime.setDuration(bambooResult.getBuildDuration());
            buildTime.setStartTime(bambooResult.getBuildStartedTime());
            return buildTime;
        } catch (BambooBuildNotFoundException e) {
            throw new BuildNotFoundException("Can't find build #" + buildId + " of project " + softwareProjectId, e);
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
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        checkConnected();
        checkSoftwareProjectId(softwareProjectId);
        checkBuildId(buildId);
        return new ArrayList<Commiter>();
    }

    private void checkBuildId(String buildId) {
        Preconditions.checkNotNull(buildId, "buildId is mandatory");
    }

    private void checkSoftwareProjectId(SoftwareProjectId softwareProjectId) {
        Preconditions.checkNotNull(softwareProjectId, "softwareProjectId is mandatory");
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        checkConnected();
        TestResult result = new TestResult();
        try {
            String planKey = projectId.getProjectId();
            int buildId = bamboo.getLastResultNumber(planKey);
            Result findResult = bamboo.findResult(planKey, buildId);
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
        checkConnected();
        return new TestResult();
    }

}
