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
package fr.norad.visuwall.providers.bamboo;

import static org.assertj.core.api.Assertions.assertThat;
import org.junit.Test;

public class BambooUrlBuilderTest {

    private String bambooUrl = "http://visuwall.norad.fr";
    private BambooUrlBuilder builder = new BambooUrlBuilder(bambooUrl);

    @Test
    public void should_build_all_projects_url() {
        String expectedUrl = "http://visuwall.norad.fr/rest/api/latest/plan";
        assertThat(builder.getAllPlansUrl()).isEqualTo(expectedUrl);
    }

    @Test
    public void should_build_latest_result_url() {
        String expectedUrl = "http://visuwall.norad.fr/rest/api/latest/result/STRUTS-STRUTS";
        assertThat(builder.getResultsUrl("STRUTS-STRUTS")).isEqualTo(expectedUrl);
    }

    @Test
    public void should_build_build_url() {
        String expectedUrl = "http://visuwall.norad.fr/rest/api/latest/result/STRUTS-STRUTS-3?expand=changes,metadata,stages,labels,jiraIssues,comments";
        assertThat(builder.getResultUrl("STRUTS-STRUTS", 3)).isEqualTo(expectedUrl);
    }

    @Test
    public void should_build_latest_build_url() {
        assertThat(builder.getAllBuildsUrl()).isEqualTo("http://visuwall.norad.fr/rest/api/latest/build");
    }

    @Test
    public void should_build_all_results_url() {
        assertThat(builder.getAllResultsUrl()).isEqualTo("http://visuwall.norad.fr/rest/api/latest/result");
    }

    @Test
    public void should_build_plan_url() {
        assertThat(builder.getPlanUrl("STRUTS-STRUTS")).isEqualTo("http://visuwall.norad.fr/rest/api/latest/plan/STRUTS-STRUTS");
    }

}
