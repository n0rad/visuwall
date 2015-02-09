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
package fr.norad.visuwall.providers.hudson;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

public class HudsonUrlBuilderTest {

    private static final String HUDSON_URL = "http://ci.visuwall.awired.net";

    HudsonUrlBuilder hudsonUrlBuilder = new HudsonUrlBuilder(HUDSON_URL);

    @Test
    public void should_create_good_pom_url() {
        String pomUrl = hudsonUrlBuilder.getPomUrl("struts");
        assertEquals("http://ci.visuwall.awired.net/job/struts/ws/pom.xml", pomUrl);
    }

    @Test
    public void should_create_good_test_url() {
        String testUrl = hudsonUrlBuilder.getTestResultUrl("struts", 5);
        assertEquals("http://ci.visuwall.awired.net/job/struts/5/testReport/api/xml", testUrl);
    }

    @Test
    public void should_create_good_all_projects_url() {
        String testUrl = hudsonUrlBuilder.getAllProjectsUrl();
        assertEquals("http://ci.visuwall.awired.net/api/xml", testUrl);
    }

    @Test
    public void should_create_good_build_url() {
        String testUrl = hudsonUrlBuilder.getBuildUrl("struts", 7);
        assertEquals("http://ci.visuwall.awired.net/job/struts/7/api/xml", testUrl);
    }

    @Test
    public void should_create_good_project_url() {
        String testUrl = hudsonUrlBuilder.getJobUrl("struts");
        assertEquals("http://ci.visuwall.awired.net/job/struts/api/xml", testUrl);
    }

    @Test
    public void should_create_good_url_with_spaces() {
        String testUrl = hudsonUrlBuilder.getBuildUrl("struts 2", 7);
        assertEquals("http://ci.visuwall.awired.net/job/struts%202/7/api/xml", testUrl);
    }

    @Test
    public void should_create_user_url() {
        String userUrl = hudsonUrlBuilder.getUserUrl("Julien Smadja");
        assertEquals("http://ci.visuwall.awired.net/user/Julien%20Smadja/api/xml", userUrl);
    }

    @Test
    public void should_create_view_url() {
        String viewUrl = hudsonUrlBuilder.getViewUrl("android");
        assertEquals("http://ci.visuwall.awired.net/view/android/api/xml", viewUrl);
    }
}
