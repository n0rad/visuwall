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

package net.awired.clients.bamboo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import net.awired.clients.bamboo.exception.BambooBuildNotFoundException;
import net.awired.clients.bamboo.exception.BambooBuildNumberNotFoundException;
import net.awired.clients.bamboo.exception.BambooEstimatedFinishTimeNotFoundException;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.exception.BambooResultNotFoundException;
import net.awired.clients.bamboo.exception.BambooStateNotFoundException;
import net.awired.clients.bamboo.resource.Build;
import net.awired.clients.bamboo.resource.Builds;
import net.awired.clients.bamboo.resource.Plan;
import net.awired.clients.bamboo.resource.Plans;
import net.awired.clients.bamboo.resource.Result;
import net.awired.clients.bamboo.resource.Results;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import org.joda.time.DateTime;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.base.Preconditions;

public class Bamboo {

    @VisibleForTesting
    BambooUrlBuilder bambooUrlBuilder;

    @VisibleForTesting
    GenericSoftwareClient client = new GenericSoftwareClient();

    private static final Logger LOG = LoggerFactory.getLogger(Bamboo.class);

    public Bamboo(String bambooUrl) {
        bambooUrlBuilder = new BambooUrlBuilder(bambooUrl);
        if (LOG.isInfoEnabled()) {
            LOG.info("Initialize bamboo with url " + bambooUrl);
        }
    }

    @Deprecated
    /***
     * Deprecated: use findAllProjects
     */
    public List<Plan> findAllPlans() {
        try {
            String projectsUrl = bambooUrlBuilder.getAllPlansUrl();
            Plans plans = client.resource(projectsUrl, Plans.class);
            return plans.plans.plan;
        } catch (ResourceNotFoundException e) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(e.getMessage(), e);
            }
            return new ArrayList<Plan>();
        }
    }

    @Deprecated
    /***
     * Deprecated: use findProject
     */
    public Plan findPlan(String planKey) throws BambooPlanNotFoundException {
        checkPlanKey(planKey);
        try {
            String planUrl = bambooUrlBuilder.getPlanUrl(planKey);
            Plan plan = client.resource(planUrl, Plan.class);
            return plan;
        } catch (ResourceNotFoundException e) {
            throw new BambooPlanNotFoundException("Can't find plan with key:" + planKey, e);
        }
    }

    public int getLastResultNumber(String planKey) throws BambooBuildNumberNotFoundException {
        checkPlanKey(planKey);
        try {
            String resultsUrl = bambooUrlBuilder.getResultsUrl(planKey);
            Results results = client.resource(resultsUrl, Results.class);
            List<Results> resultList = results.results;
            if (!resultList.isEmpty()) {
                Results subResults = resultList.get(0);
                List<Result> subResultList = subResults.result;
                if (!subResultList.isEmpty()) {
                    Result result = subResultList.get(0);
                    return result.getNumber();
                }
            }
        } catch (ResourceNotFoundException e) {
            throw new BambooBuildNumberNotFoundException("Can't find last build number of project: " + planKey, e);
        }
        throw new BambooBuildNumberNotFoundException("Can't find last build number of project: " + planKey);
    }

    public Result findResult(String projectKey, int buildNumber) throws BambooBuildNotFoundException {
        checkPlanKey(projectKey);
        try {
            String buildUrl = bambooUrlBuilder.getResultUrl(projectKey, buildNumber);
            Result result = client.resource(buildUrl, Result.class);
            return result;
        } catch (ResourceNotFoundException e) {
            throw new BambooBuildNotFoundException(e.getMessage(), e);
        }
    }

    public String getState(String projectKey) throws BambooStateNotFoundException {
        checkPlanKey(projectKey);
        try {
            int lastBuildNumber = getLastResultNumber(projectKey);
            String lastBuildUrl = bambooUrlBuilder.getAllBuildsUrl();
            Builds builds = client.resource(lastBuildUrl, Builds.class);
            List<Builds> subBuilds = builds.builds;
            if (!subBuilds.isEmpty()) {
                List<Build> allBuilds = subBuilds.get(0).build;
                String key = projectKey + "-" + lastBuildNumber;
                for (Build build : allBuilds) {
                    if (key.equals(build.key)) {
                        return build.state;
                    }
                }
            }
        } catch (ResourceNotFoundException e) {
            throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey, e);
        } catch (BambooBuildNumberNotFoundException e) {
            throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey, e);
        }
        throw new BambooStateNotFoundException("Not state found for projectKey: " + projectKey);
    }

    public Date getEstimatedFinishTime(String planKey) throws BambooPlanNotFoundException,
            BambooEstimatedFinishTimeNotFoundException {
        checkPlanKey(planKey);
        try {
            Result result;
            result = getLastResult(planKey);
            Date startTime = result.getBuildStartedTime();
            long duration = getAverageBuildDurationTime(planKey);
            DateTime startDate = new DateTime(startTime.getTime());
            DateTime estimatedFinishTime = startDate.plus(duration);
            return estimatedFinishTime.toDate();
        } catch (BambooResultNotFoundException e) {
            throw new BambooEstimatedFinishTimeNotFoundException("Can't find estimated finish time of plan:"
                    + planKey, e);
        }
    }

    long getAverageBuildDurationTime(String planKey) throws BambooPlanNotFoundException {
        checkPlanKey(planKey);
        Plan plan = findPlan(planKey);
        double averageBuildTimeInSeconds = plan.getAverageBuildTimeInSeconds();
        return (long) averageBuildTimeInSeconds;
    }

    public boolean isBuilding(String projectKey, Integer buildNumber) throws BambooPlanNotFoundException {
        checkPlanKey(projectKey);
        String planUrl = bambooUrlBuilder.getPlanUrl(projectKey);
        try {
            Plan plan = client.resource(planUrl, Plan.class);
            return plan.isBuilding();
        } catch (ResourceNotFoundException e) {
            throw new BambooPlanNotFoundException("Can't find bamboo project with projectKey " + projectKey, e);
        }
    }

    private Result getLastResult(String planKey) throws BambooResultNotFoundException {
        String resultsUrl = bambooUrlBuilder.getAllResultsUrl();
        Results resultsNode;
        try {
            resultsNode = client.resource(resultsUrl, Results.class);
        } catch (ResourceNotFoundException e) {
            throw new BambooResultNotFoundException("Can't find results at:" + resultsUrl, e);
        }
        List<Results> resultsList = resultsNode.results;
        for (Results results : resultsList) {
            for (Result result : results.result) {
                if (result.getKey().startsWith(planKey)) {
                    String resultUrl = result.getLink().href;
                    Result fullResult;
                    try {
                        fullResult = client.resource(resultUrl, Result.class);
                    } catch (ResourceNotFoundException e) {
                        throw new BambooResultNotFoundException("Can't find result at:" + resultUrl, e);
                    }
                    return fullResult;
                }
            }
        }
        throw new BambooResultNotFoundException("Can't find last result of plan:" + planKey);
    }

    private void checkPlanKey(String planKey) {
        Preconditions.checkNotNull(planKey, "planKey");
    }

}
