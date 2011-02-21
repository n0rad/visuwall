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

import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.domain.TechnicalDebt;
import com.jsmadja.wall.projectwall.service.SonarService;

public class SonarServiceITTest {

    private static final String DEV_RADAR_PROJECT_ID = "1";
    private static final String FLUXX_ARTIFACT_ID = "fr.fluxx:fluxx";

    private static SonarService sonarService = new SonarService(Integration.SONAR_URL);

    @Test
    public void should_retrieve_coverage() throws ProjectNotFoundException {
        Double coverage = sonarService.getCoverage(DEV_RADAR_PROJECT_ID);
        assertNotNull(coverage);
    }

    @Test
    public void should_retrieve_project_by_artifact_id() throws ProjectNotFoundException {
        sonarService.getCoverage(FLUXX_ARTIFACT_ID);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_does_not_exist() throws ProjectNotFoundException {
        sonarService.getCoverage("does.not.exist");
    }

    @Test
    public void should_retrieve_rules_compliance() throws ProjectNotFoundException {
        Double rulesCompliance = sonarService.getRulesCompliance(FLUXX_ARTIFACT_ID);
        assertNotNull(rulesCompliance);
    }

    @Test
    public void should_retrieve_technical_debt() throws ProjectNotFoundException {
        TechnicalDebt technicalDebt = sonarService.getTechnicalDebt(FLUXX_ARTIFACT_ID);
        assertEquals(8.9, technicalDebt.getRatio(), 0);
        assertEquals(5037, technicalDebt.getCost());
        assertEquals(10, technicalDebt.getDays());
    }
}
