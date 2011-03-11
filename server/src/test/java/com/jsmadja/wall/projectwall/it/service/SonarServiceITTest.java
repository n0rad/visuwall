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

import java.util.Map.Entry;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.domain.Project;
import com.jsmadja.wall.projectwall.domain.QualityMeasure;
import com.jsmadja.wall.projectwall.domain.QualityResult;
import com.jsmadja.wall.projectwall.service.ProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.SonarProjectNotFoundException;
import com.jsmadja.wall.projectwall.service.SonarService;

public class SonarServiceITTest {

    private static final String DEV_RADAR_ARTIFACT_ID = "com.xebia:dev-radar";
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

    @Test(expected = SonarProjectNotFoundException.class)
    public void should_throw_exception_when_passing_empty_string() throws SonarProjectNotFoundException {
        sonarService.getCoverage("");
    }

    @Test(expected = SonarProjectNotFoundException.class)
    public void should_throw_exception_when_passing_null() throws SonarProjectNotFoundException {
        sonarService.getCoverage(null);
    }

    @Test
    public void should_retrieve_rules_compliance() throws SonarProjectNotFoundException {
        Double rulesCompliance = sonarService.getRulesCompliance(FLUXX_ARTIFACT_ID);
        assertNotNull(rulesCompliance);
    }
    @Test
    public void should_populate_quality() throws SonarProjectNotFoundException, ProjectNotFoundException {
        Project project = new Project();
        project.setId(FLUXX_ARTIFACT_ID);
        QualityResult quality = new QualityResult();
        sonarService.populateQuality(project, quality, "technical_debt_ratio");
        QualityMeasure measure = quality.getMeasure("technical_debt_ratio");
        assertEquals("8.9%", measure.getFormattedValue());
        assertEquals(8.9, measure.getValue(), 0);
    }

    @Test
    public void should_have_a_lot_of_quality_metrics() throws ProjectNotFoundException {
        Project project = new Project();
        project.setId(FLUXX_ARTIFACT_ID);
        QualityResult quality = new QualityResult();
        sonarService.populateQuality(project, quality);
        Set<Entry<String, QualityMeasure>> measures = quality.getMeasures();
        for (Entry<String, QualityMeasure> measure : measures) {
            assertNotNull(measure.getValue().getValue());
        }
    }

    @Test(expected = IllegalStateException.class)
    public void should_throw_exception_if_no_url_passed() {
        new SonarService().init();
    }

    @Test
    public void should_retrieve_coverage_of_project_wall() throws SonarProjectNotFoundException {
        double coverage = sonarService.getCoverage("com.jsmadja.wall:wall");
        assertTrue(coverage > 0);
    }
}
