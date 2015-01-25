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
package fr.norad.visuwall.providers.hudson.domain;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import org.junit.Test;

public class HudsonTestTest {

    @Test
    public void is_it_when_package_contains_it() {
        HudsonTest hudsonTest = new HudsonTest("fr.norad.it.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IT() {
        HudsonTest hudsonTest = new HudsonTest("fr.norad.MyIT", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_ITest() {
        HudsonTest hudsonTest = new HudsonTest("fr.norad.MyITest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_it_when_ends_with_IntegrationTest() {
        HudsonTest hudsonTest = new HudsonTest("fr.norad.MyIntegrationTest", "SUCCESS");
        assertTrue(hudsonTest.isIntegrationTest());
    }

    @Test
    public void is_ut() {
        HudsonTest hudsonTest = new HudsonTest("fr.norad.MyTest", "SUCCESS");
        assertTrue(hudsonTest.isUnitTest());
        assertFalse(hudsonTest.isIntegrationTest());
    }

}
