package net.awired.clients.continuum;

import static org.junit.Assert.assertFalse;

import java.util.List;

import org.apache.maven.continuum.xmlrpc.project.ProjectSummary;
import org.junit.Test;

public class ContinuumITest {

    Continuum continuum = new Continuum("http://vmbuild.apache.org/continuum/xmlrpc");

    @Test
    public void should_list_projects() throws Exception {
        List<ProjectSummary> projects = continuum.findAllProjects();
        assertFalse(projects.isEmpty());
    }
}
