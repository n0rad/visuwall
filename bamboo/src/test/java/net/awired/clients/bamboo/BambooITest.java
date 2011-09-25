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

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.awired.clients.bamboo.exception.BambooBuildNotFoundException;
import net.awired.clients.bamboo.exception.BambooBuildNumberNotFoundException;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.resource.Plan;
import net.awired.clients.bamboo.resource.Result;
import net.awired.clients.common.ResourceNotFoundException;
import net.awired.clients.common.Tests;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BambooITest {

    @Parameters
    public static Collection<Object[]> createParameters() {
        String instanceProperty = "bambooInstances";
        return Tests.createUrlInstanceParametersFromProperty(instanceProperty);
    }

    private Bamboo bamboo;

    public BambooITest(String bambooUrl) {
        System.out.println("Testing Bamboo url:'" + bambooUrl + "'");
        this.bamboo = new Bamboo(bambooUrl);
    }

    @Test
    public void should_find_an_existing_plan() throws BambooPlanNotFoundException {
        Plan plan = getFirstPlanOfTheInstance();
        Plan project = bamboo.findPlan(plan.getKey());
        assertNotNull(project);
    }

    @Test
    public void should_find_all_plans() throws ResourceNotFoundException {
        List<Plan> projects = bamboo.findAllPlans();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();

        String planKey = "ZOO-ZOOKEY";
        int lastBuildNumber = bamboo.getLastResultNumber(planKey);
        assertEquals(10, lastBuildNumber);
        assertTrue(bamboo.isBuilding(planKey, 0));
        System.out.println(bamboo.getAverageBuildDurationTime(planKey));
        System.out.println(bamboo.getEstimatedFinishTime(planKey));
    }

    @Test(expected = BambooPlanNotFoundException.class)
    public void should_throw_exception_when_plan_not_found() throws Exception {
        bamboo.findPlan("planKey");
    }

    @Test(expected = BambooBuildNumberNotFoundException.class)
    public void should_throw_exception_when_build_not_found_for_finding_last_build_number() throws Exception {
        bamboo.getLastResultNumber("projectKey");
    }

    @Test(expected = BambooBuildNotFoundException.class)
    public void should_throw_exception_when_build_not_found() throws Exception {
        bamboo.findResult("projectKey", 0);
    }

    @Test
    public void should_find_result() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();
        int lastBuildNumber = getLastBuildNumberOfTheFirstPlan();

        Result result = bamboo.findResult(plan.getKey(), lastBuildNumber);

        assertNotNull(result);
        assertEquals(lastBuildNumber, result.getNumber());
        assertTrue(result.getBuildDuration() > 0);
        assertTrue(result.getFailedTestCount() >= 0);
        assertTrue(0 < result.getSuccessfulTestCount());
        assertNotNull(result.getBuildStartedTime());
        assertNotNull(result.getState());
    }

    @Test
    public void should_find_state() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();
        String state = bamboo.getState(plan.getKey());

        assertNotNull(state);
    }

    @Test
    public void should_get_average_build_duration_time() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();

        long duration = bamboo.getAverageBuildDurationTime(plan.getKey());
        System.out.println(duration);
        assertTrue(duration > 0);
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();

        Date finishTime = bamboo.getEstimatedFinishTime(plan.getKey());

        assertNotNull(finishTime);
    }

    @Test
    public void should_get_is_building() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();
        int buildNumber = getLastBuildNumberOfTheFirstPlan();

        boolean isBuilding = bamboo.isBuilding(plan.getKey(), buildNumber);

        assertFalse(isBuilding);
    }

    @Test
    public void should_find_project() throws Exception {
        Plan plan = getFirstPlanOfTheInstance();

        plan = bamboo.findPlan(plan.getKey());

        assertTrue(plan.isEnabled());
        assertNotNull(plan.getType());
        assertNotNull(plan.getName());
        assertNotNull(plan.getKey());
        assertNotNull(plan.getProjectKey());
        assertNotNull(plan.getProjectName());
        assertNotNull(plan.getLink());
        assertFalse(plan.isFavourite());
        assertFalse(plan.isActive());
        assertFalse(plan.isBuilding());
        assertTrue(plan.getAverageBuildTimeInSeconds() > 0);
    }

    private Plan getFirstPlanOfTheInstance() {
        List<Plan> plans = bamboo.findAllPlans();
        Plan plan = plans.get(0);
        return plan;
    }

    private int getLastBuildNumberOfTheFirstPlan() throws BambooBuildNumberNotFoundException {
        Plan plan = getFirstPlanOfTheInstance();
        int lastBuildNumber = bamboo.getLastResultNumber(plan.getKey());
        return lastBuildNumber;
    }

}
