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
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import net.awired.clients.bamboo.exception.BambooBuildNotFoundException;
import net.awired.clients.bamboo.exception.BambooBuildNumberNotFoundException;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.resource.Link;
import net.awired.clients.bamboo.resource.Plan;
import net.awired.clients.bamboo.resource.Result;
import net.awired.clients.common.ResourceNotFoundException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;

@RunWith(Parameterized.class)
public class BambooTest {

    @Parameters
    public static Collection<Object[]> createParameters() {
        Object[] bambooTargets = new Object[] { //
        "http://bamboo.visuwall.awired.net", "X.Y.Z" // 
        };
        Object[][] data = new Object[][] { bambooTargets };
        return Arrays.asList(data);
    }

    private Bamboo bamboo;
    private String bambooUrl;
    private String bambooVersion;

    public BambooTest(String bambooUrl, String bambooVersion) {
        System.out.println("Testing Bamboo url:'" + bambooUrl + "', version:'" + bambooVersion + "'");
        this.bamboo = new Bamboo(bambooUrl);
        this.bambooUrl = bambooUrl;
        this.bambooVersion = bambooVersion;
    }

    @Test
    public void should_find_an_existing_plan() throws BambooPlanNotFoundException {
        Plan project = bamboo.findPlan("STRUTS-STRUTS");
        assertNotNull(project);
    }

    @Test
    public void should_find_all_plans() throws ResourceNotFoundException {
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
        //        when(client.resource(anyString(), any(Class.class))).thenThrow(new ResourceNotFoundException("not found"));
        //
        //        List<Plan> projects = bamboo.findAllPlans();
        //
        //        assertTrue(projects.isEmpty());
    }

    @Test
    public void should_find_last_build_number() throws Exception {
        int lastBuildNumber = bamboo.getLastResultNumber("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");
        assertEquals(1, lastBuildNumber);
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
        String state = bamboo.getState("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals("Successful", state);
    }

    @Test
    public void should_get_average_build_duration_time() throws Exception {
        long duration = bamboo.getAverageBuildDurationTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertEquals(114, duration);
    }

    @Test
    public void should_get_estimated_finish_time() throws Exception {
        Date finishTime = bamboo.getEstimatedFinishTime("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6");

        assertNotNull(finishTime);
    }

    @Test
    public void should_get_is_building() throws Exception {
        boolean isBuilding = bamboo.isBuilding("AJSL-AWIREDJAVASTANDARDLIBRARY10ALPHA6", 0);

        assertTrue(isBuilding);
    }

    @Test
    public void should_find_project() throws Exception {
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

}
