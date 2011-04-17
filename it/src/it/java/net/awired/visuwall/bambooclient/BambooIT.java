package net.awired.visuwall.bambooclient;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.IntegrationTestData;
import net.awired.visuwall.bambooclient.domain.BambooBuild;
import net.awired.visuwall.bambooclient.domain.BambooProject;

import org.junit.Test;


public class BambooIT {

    private Bamboo bamboo = new Bamboo(IntegrationTestData.BAMBOO_URL);

    @Test
    public void should_find_all_projects() {
        List<BambooProject> projects = bamboo.findAllProjects();
        assertFalse(projects.isEmpty());
    }

    @Test
    public void should_find_an_existing_project() {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        assertNotNull(project);
    }

    @Test
    public void should_fill_current_build_when_finding_project() {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        BambooBuild build = project.getCurrentBuild();
        assertEquals(3, build.getBuildNumber());
    }

    @Test
    public void should_fill_build_numbers_when_finding_project() {
        BambooProject project = bamboo.findProject("STRUTS-STRUTS");
        int[] buildNumbers = project.getBuildNumbers();
        assertEquals(buildNumbers[0], 3);
        assertEquals(buildNumbers[1], 2);
    }
}
