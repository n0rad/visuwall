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
package fr.norad.visuwall.providers.pivotal;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

public class PivotalTrackerUrlBuilderTest {

    private PivotalTrackerUrlBuilder builder = new PivotalTrackerUrlBuilder("https://www.pivotaltracker.com");

    @Test
    public void build_authentication_token_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/tokens/active", builder.getAuthenticationTokenUrl());
    }

    @Test
    public void build_all_projects_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects", builder.getAllProjectsUrl());
    }

    @Test
    public void build_project_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects/1234", builder.getProjectUrl(1234));
    }

    @Test
    public void build_all_stories_url() {
        assertEquals("https://www.pivotaltracker.com/services/v3/projects/506819/stories",
                builder.getAllStoriesUrl(506819));
    }

}
