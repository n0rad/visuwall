/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com>
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

package net.awired.clients.hudson.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HudsonTestTest {

    @Test
    public void is_it_when_package_contains_it() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.it.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IT() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyIT", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_ITest() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyITest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IntegrationTest() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyIntegrationTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_ut() {
        HudsonTest hudsonTest = new HudsonTest("net.awired.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isUnitTest());
        assertFalse(hudsonTest.isIntegrationTest());
    }

}
