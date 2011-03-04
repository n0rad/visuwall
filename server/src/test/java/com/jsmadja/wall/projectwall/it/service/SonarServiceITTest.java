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

package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.junit.Before;
import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.TechnicalDebt;
import com.jsmadja.wall.projectwall.service.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.SonarProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.SonarService;

public class SonarServiceITTest {

    private static final String DEV_RADAR_ARTIFACT_ID = "com.xebia:dev-radar";
    private static final String DEV_RADAR_PROJECT_ID = "1";
    private static final String FLUXX_ARTIFACT_ID = "fr.fluxx:fluxx";

    private static SonarService sonarService;

    @Before
    public void init() {
        sonarService = new SonarService();
        sonarService.setUrl(Integration.SONAR_URL);
        sonarService.init();
    }

    @Test
    public void should_retrieve_coverage() throws SonarProjectNotFoundException {
        double coverage = sonarService.getCoverage(DEV_RADAR_ARTIFACT_ID);
        assertTrue(coverage > 0);
    }

    @Test
    public void should_retrieve_project_by_artifact_id() throws SonarProjectNotFoundException {
        double coverage = sonarService.getCoverage(FLUXX_ARTIFACT_ID);
        assertTrue(coverage > 0);
    }

    @Test(expected = SonarProjectNotFoundException.class)
    public void should_throw_exception_when_project_does_not_exist() throws SonarProjectNotFoundException {
        sonarService.getCoverage("does.not.exist");
    }

    @Test
    public void should_retrieve_rules_compliance() throws SonarProjectNotFoundException {
        Double rulesCompliance = sonarService.getRulesCompliance(FLUXX_ARTIFACT_ID);
        assertNotNull(rulesCompliance);
    }

    @Test
    public void should_retrieve_technical_debt() throws SonarProjectNotFoundException {
        TechnicalDebt technicalDebt = sonarService.getTechnicalDebt(FLUXX_ARTIFACT_ID);
        assertEquals(8.9, technicalDebt.getRatio(), 0);
        assertEquals(5037, technicalDebt.getCost());
        assertEquals(10, technicalDebt.getDays());
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url_passed() {
        new SonarService().init();
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_for_estimated_finish_time() throws ProjectNotFoundException {
        sonarService.getEstimatedFinishTime(new Project());
    }

    @Test(expected = RuntimeException.class)
    public void should_throw_exception_when_searching_by_name() throws ProjectNotFoundException {
        sonarService.findProjectByName("");
    }

    @Test
    public void should_return_empty_list() {
        assertTrue(sonarService.findAllProjects().isEmpty());
    }

    @Test
    public void should_populate_project() throws ProjectNotFoundException {
        Project project = new Project();
        project.setId(FLUXX_ARTIFACT_ID);

        assertTrue(project.getRulesCompliance() == 0);

        sonarService.populate(project);

        assertTrue(project.getRulesCompliance() > 0);

    }
}
