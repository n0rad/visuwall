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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import java.util.List;
import net.awired.clients.bamboo.Bamboo;
import net.awired.clients.bamboo.exception.BambooPlanNotFoundException;
import net.awired.clients.bamboo.rest.Plan;
import org.junit.Test;

public class BambooIT {

    private Bamboo bamboo = new Bamboo("http://bamboo.visuwall.awired.net");

    @Test
    public void should_find_all_plans() {
        List<Plan> plans = bamboo.findAllPlans();
        assertFalse(plans.isEmpty());
    }

    @Test
    public void should_find_an_existing_plan() throws BambooPlanNotFoundException {
        Plan project = bamboo.findPlan("STRUTS-STRUTS");
        assertNotNull(project);
    }

}
