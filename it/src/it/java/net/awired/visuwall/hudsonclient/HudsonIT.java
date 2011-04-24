/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.hudsonclient;

import net.awired.visuwall.IntegrationTestData;

import org.junit.Test;


public class HudsonIT {

    private Hudson hudson = new Hudson(IntegrationTestData.HUDSON_URL);

    @Test(expected = HudsonBuildNotFoundException.class)
    public void should_throw_an_exception_when_searching_an_inexistant_build() throws HudsonBuildNotFoundException, HudsonProjectNotFoundException {
        hudson.findBuild("neverbuild", -1);
    }

    @Test
    public void should_find_not_built_project() throws HudsonProjectNotFoundException {
        hudson.findProject("neverbuild");
    }

    @Test(expected=HudsonProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project() throws HudsonProjectNotFoundException {
        hudson.findProject("");
    }

    @Test(expected=HudsonProjectNotFoundException.class)
    public void should_throw_exception_when_searching_inexistant_project_with_build_no() throws HudsonProjectNotFoundException, HudsonBuildNotFoundException {
        hudson.findBuild("", 0);
    }

}
