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

package fr.norad.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class TestResultExtractorTest {

    @Test
    public void should_extract_all() {
        String statusText = "Tests failed: 1 (1 new), passed: 3, ignored: 5";

        int failed = TestResultExtractor.extractFailed(statusText);
        int passed = TestResultExtractor.extractPassed(statusText);
        int ignored = TestResultExtractor.extractIgnored(statusText);

        assertEquals(1, failed);
        assertEquals(3, passed);
        assertEquals(5, ignored);
    }

    @Test
    public void should_extract_passed() {
        String statusText = "Tests passed: 331";

        int failed = TestResultExtractor.extractFailed(statusText);
        int passed = TestResultExtractor.extractPassed(statusText);
        int ignored = TestResultExtractor.extractIgnored(statusText);

        assertEquals(0, failed);
        assertEquals(331, passed);
        assertEquals(0, ignored);
    }
}
