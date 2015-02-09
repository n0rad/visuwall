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

package fr.norad.visuwall.plugin.sonar;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class SonarCompatibleVersionCheckerTest {

    SonarCompatibleVersionChecker checker = new SonarCompatibleVersionChecker(2.4);

    @Test
    public void should_get_incompatible_for_1_0_version() {
        assertFalse(checker.versionIsCompatible(1.0));
    }

    @Test
    public void should_get_incompatible_for_2_0_version() {
        assertFalse(checker.versionIsCompatible(2.0));
    }

    @Test
    public void should_get_incompatible_for_2_3_version() {
        assertFalse(checker.versionIsCompatible(2.3));
    }

    @Test
    public void should_get_compatible_for_3_0_version() {
        assertTrue(checker.versionIsCompatible(3.0));
    }

    @Test
    public void should_get_compatible_for_2_4_version() {
        assertTrue(checker.versionIsCompatible(2.4));
    }

    @Test
    public void should_get_compatible_for_2_5_version() {
        assertTrue(checker.versionIsCompatible(2.5));
    }

    @Test
    public void should_get_compatible_for_2_12_version() {
        assertTrue(checker.versionIsCompatible(2.12));
    }

    @Test
    public void should_get_compatible_for_2_4_version_as_string() {
        assertTrue(checker.versionIsCompatible("2.4"));
    }

    @Test
    public void should_get_compatible_for_2_12_version_as_string() {
        assertTrue(checker.versionIsCompatible("2.12"));
    }
}
