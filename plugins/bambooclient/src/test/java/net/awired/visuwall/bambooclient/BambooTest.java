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

package net.awired.visuwall.bambooclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyInt;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.when;
import java.util.Date;
import java.util.List;
import net.awired.visuwall.bambooclient.builder.BambooUrlBuilder;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;
import net.awired.visuwall.bambooclient.exception.BambooBuildNotFoundException;
import net.awired.visuwall.bambooclient.exception.BambooBuildNumberNotFoundException;
import net.awired.visuwall.bambooclient.rest.Builds;
import net.awired.visuwall.bambooclient.rest.Plan;
import net.awired.visuwall.bambooclient.rest.Plans;
import net.awired.visuwall.bambooclient.rest.Result;
import net.awired.visuwall.bambooclient.rest.Results;
import net.awired.visuwall.common.client.GenericSoftwareClient;
import net.awired.visuwall.common.client.ResourceNotFoundException;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

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
    public void should_find_all_projects() throws ResourceNotFoundException {
        Plans plans = createPlans();
        when(client.resource(anyString(), any(Class.class))).thenReturn(plans);

        List<BambooProject> projects = bamboo.findAllProjects();

        BambooProject ajsl = projects.get(0);
        assertEquals("ajsl - Awired Java Standard Library 1.0-ALPHA6", ajsl.getName());
        assertEquals("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", ajsl.getKey());
        assertEquals("http://bamboo.visuwall.awired.net/rest/api/latest/plan/AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6",
                ajsl.getLink());
    }

    @Test
    public void should_return_empty_list_if_plans_are_not_found() throws ResourceNotFoundException {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        List<BambooProject> projects = bamboo.findAllProjects();

        assertTrue(projects.isEmpty());
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        Results results = createResults();
        when(client.resource(anyString(), any(Class.class))).thenReturn(results);

        int lastBuildNumber = bamboo.getLastBuildNumber("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals(1, lastBuildNumber);
    }

    @Test(expected = BambooBuildNumberNotFoundException.class)
    public void should_throw_exception_when_build_not_found_for_finding_last_build_number() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        bamboo.getLastBuildNumber("projectKey");
    }

    @Test(expected = BambooBuildNotFoundException.class)
    public void should_throw_exception_when_build_not_found() throws Exception {
        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));

        bamboo.findBuild("projectKey", 0);
    }

    @Test
    public void should_find_build() throws Exception {
        Result result = createResult();
        when(client.resource(anyString(), any(Class.class))).thenReturn(result);

        BambooBuild build = bamboo.findBuild("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", 1);

        assertNotNull(build);
        assertEquals(1, build.getBuildNumber());
        assertEquals(114817, build.getDuration());
        assertEquals(0, build.getFailCount());
        assertEquals(18, build.getPassCount());
        assertNotNull(build.getStartTime());
        assertEquals("Successful", build.getState());
    }

    @Test
    public void should_find_state() throws Exception {
        Builds builds = createBuilds();
        when(bambooUrlBuilder.getLastBuildUrl()).thenReturn("last-build-url");
        when(client.resource(eq("last-build-url"), any(Class.class))).thenReturn(builds);

        Results results = createResults();
        when(bambooUrlBuilder.getLatestBuildResult(anyString())).thenReturn("latest-build-result-url");
        when(client.resource(eq("latest-build-result-url"), any(Class.class))).thenReturn(results);

        String state = bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals("Successful", state);
    }

    @Test
    public void should_get_average_build_duration_time() throws Exception {
        when(bambooUrlBuilder.getProjectUrl(anyString())).thenReturn("project-url");
        Results results = createResults();
        when(client.resource(eq("project-url"), any(Class.class))).thenReturn(results);

        when(bambooUrlBuilder.getIsBuildingUrl(anyString())).thenReturn("is-building-url");
        Plan plan = createPlan();
        when(client.resource(eq("is-building-url"), any(Class.class))).thenReturn(plan);

        when(bambooUrlBuilder.getBuildUrl(anyString(), anyInt())).thenReturn("build-url");
        Result result = createResult();
        when(client.resource(eq("build-url"), any(Class.class))).thenReturn(result);

        long duration = bamboo.getAverageBuildDurationTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals(114817, duration);
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        when(bambooUrlBuilder.getProjectUrl(anyString())).thenReturn("project-url");
        Results results = createResults();
        when(client.resource(eq("project-url"), any(Class.class))).thenReturn(results);

        when(bambooUrlBuilder.getIsBuildingUrl(anyString())).thenReturn("is-building-url");
        Plan plan = createPlan();
        when(client.resource(eq("is-building-url"), any(Class.class))).thenReturn(plan);

        when(bambooUrlBuilder.getBuildUrl(anyString(), anyInt())).thenReturn("build-url");
        Result result = createResult();
        when(client.resource(eq("build-url"), any(Class.class))).thenReturn(result);

        Date finishTime = bamboo.getEstimatedFinishTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertNotNull(finishTime);
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
