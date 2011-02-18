package com.jsmadja.wall.projectwall.it.service;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

import com.jsmadja.wall.projectwall.Integration;
import com.jsmadja.wall.projectwall.ProjectNotFoundException;
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
}
