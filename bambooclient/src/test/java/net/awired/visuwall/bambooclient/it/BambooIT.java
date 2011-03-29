package net.awired.visuwall.bambooclient.it;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;

import java.util.List;

import net.awired.visuwall.bambooclient.Bamboo;
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
}
