package com.jsmadja.wall.projectwall;

import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class SonarServiceITTest {

    private static final String ON_API_COVER_PROJECT_ID = "1029";
    private static final String SYNTHESIS_ARTIFACT_ID = "com.orangevallee.on.server.synthesis:synthesis";

    private static SonarService sonarService = new SonarService(Integration.SONAR_URL);

    @Test
    public void should_retrieve_coverage() throws ProjectNotFoundException {
        Double coverage = sonarService.getCoverage(ON_API_COVER_PROJECT_ID);
        assertNotNull(coverage);
    }

    @Test
    public void should_retrieve_project_by_artifact_id() throws ProjectNotFoundException {
        sonarService.getCoverage(SYNTHESIS_ARTIFACT_ID);
    }

    @Test
    public void should_retrieve_project_by_name() throws ProjectNotFoundException {
        sonarService.getCoverage("Synthesis");
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_project_does_not_exist() throws ProjectNotFoundException {
        sonarService.getCoverage("does.not.exist");
    }

    @Test
    public void should_retrieve_rules_compliance() throws ProjectNotFoundException {
        Double rulesCompliance = sonarService.getRulesCompliance(SYNTHESIS_ARTIFACT_ID);
        assertNotNull(rulesCompliance);
    }
}
