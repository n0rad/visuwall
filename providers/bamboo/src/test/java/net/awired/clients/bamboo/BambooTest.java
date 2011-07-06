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
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.List;
import net.awired.clients.bamboo.Bamboo;
import net.awired.clients.bamboo.builder.BambooUrlBuilder;
import net.awired.clients.bamboo.exception.BambooBuildNotFoundException;
import net.awired.clients.bamboo.exception.BambooBuildNumberNotFoundException;
import net.awired.clients.bamboo.exception.BambooEstimatedFinishTimeNotFoundException;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.exception.BambooStateNotFoundException;
import net.awired.clients.bamboo.rest.Builds;
import net.awired.clients.bamboo.rest.Link;
import net.awired.clients.bamboo.rest.Plan;
import net.awired.clients.bamboo.rest.Plans;
import net.awired.clients.bamboo.rest.Result;
import net.awired.clients.bamboo.rest.Results;
import net.awired.clients.common.GenericSoftwareClient;
import net.awired.clients.common.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

@SuppressWarnings("unchecked")
public class BambooTest {

    BambooUrlBuilder bambooUrlBuilder;
    GenericSoftwareClient client;
    Bamboo bamboo;

    @Before
    public void init() {
        bambooUrlBuilder = Mockito.mock(BambooUrlBuilder.class);
        client = Mockito.mock(GenericSoftwareClient.class);
        bamboo = new Bamboo("http://bamboo.com");
        bamboo.bambooUrlBuilder = bambooUrlBuilder;
        bamboo.client = client;
    }

    @Test
    public void should_find_all_plans() throws ResourceNotFoundException {
        Plans plans = createPlans();
        when(client.resource(anyString(), any(Class.class))).thenReturn(plans);

        List<Plan> projects = bamboo.findAllPlans();

        Link link = new Link();
        link.href = "http://bamboo.visuwall.awired.net/rest/api/latest/plan/AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6";
        link.rel = "self";

        Plan ajsl = projects.get(0);
        assertEquals("ajsl - Awired Java Standard Library 1.0-ALPHA6", ajsl.getName());
        assertEquals("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", ajsl.getKey());
        assertEquals(link, ajsl.getLink());
    }

    @Test
    public void should_return_empty_list_if_plans_are_not_found() throws ResourceNotFoundException {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        List<Plan> projects = bamboo.findAllPlans();

        assertTrue(projects.isEmpty());
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        Results results = createResults();
        when(client.resource(anyString(), any(Class.class))).thenReturn(results);

        int lastBuildNumber = bamboo.getLastResultNumber("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals(1, lastBuildNumber);
    }

    @Test(expected = BambooPlanNotFoundException.class)
    public void should_throw_exception_when_plan_not_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        bamboo.findPlan("planKey");
    }

    @Test(expected = BambooBuildNumberNotFoundException.class)
    public void should_throw_exception_when_build_not_found_for_finding_last_build_number() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        bamboo.getLastResultNumber("projectKey");
    }

    @Test(expected = BambooBuildNotFoundException.class)
    public void should_throw_exception_when_build_not_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        bamboo.findResult("projectKey", 0);
    }

    @Test
    public void should_find_result() throws Exception {
        Result expectedResult = createResult();
        when(client.resource(anyString(), any(Class.class))).thenReturn(expectedResult);

        Result result = bamboo.findResult("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", 1);

        assertNotNull(result);
        assertEquals(1, result.getNumber());
        assertEquals(114817, result.getBuildDuration());
        assertEquals(0, result.getFailedTestCount());
        assertEquals(18, result.getSuccessfulTestCount());
        assertNotNull(result.getBuildStartedTime());
        assertEquals("Successful", result.getState());
    }

    @Test
    public void should_find_state() throws Exception {
        Builds builds = createBuilds();
        when(bambooUrlBuilder.getAllBuildsUrl()).thenReturn("last-build-url");
        when(client.resource(eq("last-build-url"), any(Class.class))).thenReturn(builds);

        Results results = createResults();
        when(bambooUrlBuilder.getResultsUrl(anyString())).thenReturn("latest-build-result-url");
        when(client.resource(eq("latest-build-result-url"), any(Class.class))).thenReturn(results);

        String state = bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals("Successful", state);
    }

    @Test(expected = BambooStateNotFoundException.class)
    public void should_throw_exception_when_builds_are_not_found() throws Exception {
        Throwable notFound = new ResourceNotFoundException("not found");
        when(bambooUrlBuilder.getAllBuildsUrl()).thenReturn("last-build-url");
        when(client.resource(eq("last-build-url"), any(Class.class))).thenThrow(notFound);

        Results results = createResults();
        when(bambooUrlBuilder.getResultsUrl(anyString())).thenReturn("latest-build-result-url");
        when(client.resource(eq("latest-build-result-url"), any(Class.class))).thenReturn(results);

        bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");
    }

    @Test(expected = BambooStateNotFoundException.class)
    public void should_throw_exception_when_there_is_no_builds() throws Exception {
        Builds builds = new Builds();
        when(bambooUrlBuilder.getAllBuildsUrl()).thenReturn("last-build-url");
        when(client.resource(eq("last-build-url"), any(Class.class))).thenReturn(builds);

        Results results = createResults();
        when(bambooUrlBuilder.getResultsUrl(anyString())).thenReturn("latest-build-result-url");
        when(client.resource(eq("latest-build-result-url"), any(Class.class))).thenReturn(results);

        bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");
    }

    @Test(expected = BambooStateNotFoundException.class)
    public void should_throw_exception_when_build_number_is_not_found() throws Exception {
        Throwable notFound = new ResourceNotFoundException("not found");
        when(client.resource(anyString(), any(Class.class))).thenThrow(notFound);
        bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");
    }

    @Test
    public void should_get_average_build_duration_time() throws Exception {
        when(bambooUrlBuilder.getPlanUrl(anyString())).thenReturn("plan-url");
        when(client.resource(eq("plan-url"), any(Class.class))).thenReturn(createPlan());

        long duration = bamboo.getAverageBuildDurationTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals(114, duration);
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        when(bambooUrlBuilder.getAllResultsUrl()).thenReturn("results-url");

        Results results = createResults();
        when(client.resource(eq("results-url"), any(Class.class))).thenReturn(results);

        Result result = createResult();
        String resultUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/result/AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6-1";
        when(client.resource(eq(resultUrl), any(Class.class))).thenReturn(result);

        when(bambooUrlBuilder.getPlanUrl(anyString())).thenReturn("plan-url");
        when(client.resource(eq("plan-url"), any(Class.class))).thenReturn(createPlan());

        Date finishTime = bamboo.getEstimatedFinishTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertNotNull(finishTime);
    }

    @Test
    public void should_get_is_building() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenReturn(createPlan());

        boolean isBuilding = bamboo.isBuilding("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", 0);

        assertTrue(isBuilding);
    }

    @Test
    public void should_find_project() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenReturn(createPlan());

        Plan plan = bamboo.findPlan("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        Link link = new Link();
        link.rel = "self";
        link.href = "http://bamboo.visuwall.awired.net/rest/api/latest/plan/AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6";

        assertTrue(plan.isEnabled());
        assertEquals("chain", plan.getType());
        assertEquals("ajsl - Awired Java Standard Library 1.0-ALPHA6", plan.getName());
        assertEquals("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", plan.getKey());
        assertEquals("AJSL", plan.getProjectKey());
        assertEquals("ajsl", plan.getProjectName());
        assertEquals(link, plan.getLink());
        assertTrue(plan.isFavourite());
        assertTrue(plan.isActive());
        assertTrue(plan.isBuilding());
        assertEquals(114.0d, plan.getAverageBuildTimeInSeconds(), 0);
    }

    @Test(expected = BambooBuildNumberNotFoundException.class)
    public void should_throw_exception_if_there_is_no_results_to_get_last_result_number() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenReturn(new Results());

        bamboo.getLastResultNumber("projectKey");
    }

    @Test(expected = BambooEstimatedFinishTimeNotFoundException.class)
    public void should_throw_exception_when_there_is_no_result_to_get_estimated_finish_time() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        bamboo.getEstimatedFinishTime("planKey");
    }

    @Test(expected = BambooPlanNotFoundException.class)
    public void should_throw_exception_when_there_is_no_plan_to_get_average_build_duration_time() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        bamboo.getAverageBuildDurationTime("planKey");
    }

    @Test(expected = BambooPlanNotFoundException.class)
    public void should_throw_exception_when_there_is_no_plan_to_get_is_building() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        bamboo.isBuilding("planKey", 0);
    }

    @Test(expected = BambooBuildNumberNotFoundException.class)
    public void should_throw_exception_when_there_is_no_plan_to_get_last_result() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        bamboo.getLastResultNumber("planKey");
    }

    private Builds createBuilds() {
        return (Builds) ClasspathFiles.load("rest/api/latest/build.xml", Builds.class);
    }

    private Results createResults() {
        return (Results) ClasspathFiles.load("rest/api/latest/result.xml", Results.class);
    }

    private Plans createPlans() {
        return (Plans) ClasspathFiles.load("rest/api/latest/plan.xml", Plans.class);
    }

    private Plan createPlan() {
        return (Plan) ClasspathFiles.load("rest/api/latest/plan/ajsl.xml", Plan.class);
    }

    private Result createResult() {
        return (Result) ClasspathFiles.load("rest/api/latest/result/ajsl.xml", Result.class);
    }

}
