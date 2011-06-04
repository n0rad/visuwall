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

package net.awired.visuwall.bambooclient.builder;

import static org.junit.Assert.assertEquals;

import org.junit.Test;


public class BambooUrlBuilderTest {

    private String bambooUrl = "http://bamboo.visuwall.awired.net";
    private BambooUrlBuilder builder = new BambooUrlBuilder(bambooUrl);

    @Test
    public void should_build_all_projects_url() {
        String expectedUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/plan";
        assertEquals(expectedUrl, builder.getAllProjectsUrl());
    }

    @Test
    public void should_build_latest_result_url() {
        String expectedUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/result/STRUTS-STRUTS";
        assertEquals(expectedUrl, builder.getLatestBuildResult("STRUTS-STRUTS"));
    }

    @Test
    public void should_build_build_url() {
        String expectedUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/result/STRUTS-STRUTS-3?expand=changes,metadata,stages,labels,jiraIssues,comments";
        assertEquals(expectedUrl, builder.getBuildUrl("STRUTS-STRUTS", 3));
    }

    @Test
    public void should_build_is_building_url() {
        String expectedUrl = "http://bamboo.visuwall.awired.net/rest/api/latest/plan/STRUTS2INSTABLE-STRUTS2INSTABLE";
        assertEquals(expectedUrl, builder.getIsBuildingUrl("STRUTS2INSTABLE-STRUTS2INSTABLE"));
    }

    @Test
    public void should_build_latest_build_url() {
        assertEquals("http://bamboo.visuwall.awired.net/rest/api/latest/build", builder.getLastBuildUrl());
    }

}
