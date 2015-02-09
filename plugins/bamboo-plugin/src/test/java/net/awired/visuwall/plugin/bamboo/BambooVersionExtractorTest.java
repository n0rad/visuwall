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

package fr.norad.visuwall.plugin.bamboo;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class BambooVersionExtractorTest {

    @Test
    public void should_extract_version() throws Exception {
        String content = "Atlassian Bamboo</a> version 2.7.1 build 2101 -";
        String version = BambooVersionExtractor.extractVersion(content);
        assertEquals("2.7.1", version);
    }

    @Test(expected = BambooVersionNotFoundException.class)
    public void should_throw_exception_if_version_is_not_found() throws BambooVersionNotFoundException {
        BambooVersionExtractor.extractVersion("");
    }

}
