package net.awired.visuwall.plugin.deployit;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;

import org.junit.BeforeClass;
import org.junit.Test;

public class DeployItConnectionIT {

    static DeployItConnection connection;

    @BeforeClass
    public static void init() throws ConnectionException {
        connection = new DeployItConnection();
        connection.connect("http://localhost:4516", "admin", "admin");
    }

    @Test(expected = MavenIdNotFoundException.class)
    public void should_throw_exception_when_getting_maven_id() throws ProjectNotFoundException,
            MavenIdNotFoundException {
        connection.getMavenId(null);
    }

    @Test(expected = ProjectNotFoundException.class)
    public void should_throw_exception_when_identifying() throws ProjectNotFoundException, ProjectNotFoundException {
        connection.identify(null);
    }

    @Test
    public void is_never_building() throws ProjectNotFoundException, BuildNotFoundException {
        boolean building = connection.isBuilding(null, null);
        assertFalse(building);
    }

    @Test
    public void is_never_disabled() throws ProjectNotFoundException, BuildNotFoundException {
        boolean disabled = connection.isProjectDisabled(null);
        assertFalse(disabled);
    }

    @Test
    public void no_description() throws ProjectNotFoundException {
        String description = connection.getDescription(null);
        assertEquals("", description);
    }

    @Test
    public void should_list_projects_ids() {
        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        Collection<String> applications = softwareProjectIds.values();
        for (String application : applications) {
            System.out.println(application);
        }
    }

    @Test
    public void should_get_name() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("Tomcat-Dev - XkeTomcatPet");
        String name = connection.getName(projectId);
        assertEquals("Tomcat-Dev<br/>XkeTomcatPet v34", name);

        System.out.println(Arrays.toString(connection.getBuildIds(projectId).toArray()));
        System.out.println(connection.getBuildTime(new SoftwareProjectId("Tomcat-Production - XkeTomcatPet"), "22"));
    }

    public void should_get_num_of_pass_steps() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("Tomcat-Dev - XkeTomcatPet");
        TestResult steps = connection.analyzeUnitTests(projectId);
        assertEquals(0, steps.getSkipCount());
        assertEquals(8, steps.getPassCount());
        assertEquals(0, steps.getFailCount());
        assertEquals(8, steps.getTotalCount());
        assertEquals(0, steps.getCoverage(), 0);
    }

    @Test
    public void should_not_analyze_integration_tests() throws Exception {
        SoftwareProjectId projectId = new SoftwareProjectId("Tomcat-Dev - XkeTomcatPet");
        TestResult steps = connection.analyzeIntegrationTests(projectId);
        assertEquals(0, steps.getSkipCount());
        assertEquals(0, steps.getPassCount());
        assertEquals(0, steps.getFailCount());
        assertEquals(0, steps.getTotalCount());
        assertEquals(0, steps.getCoverage(), 0);
    }

    @Test
    public void should_find_views() throws Exception {
        List<String> views = connection.findViews();
        assertEquals(4, views.size());
        assertTrue(views.contains("Tomcat-Dev"));
        assertTrue(views.contains("Tomcat-Production"));
        assertTrue(views.contains("Tomcat-Integration"));
        assertTrue(views.contains("Tomcat-QA"));
    }

    @Test
    public void should_retrieve_software_by_view() throws Exception {
        List<String> projectNames = connection.findProjectNamesByView("Tomcat-Dev");
        assertEquals(1, projectNames.size());
        assertTrue(projectNames.contains("Tomcat-Dev/XkeTomcatPet"));
    }

    @Test
    public void should_find_software_project_ids_by_views() throws Exception {
        SoftwareProjectId dev = new SoftwareProjectId("Tomcat-Dev - XkeTomcatPet");
        SoftwareProjectId integration = new SoftwareProjectId("Tomcat-Integration - XkeTomcatPet");
        List<String> views = Arrays.asList("Tomcat-Dev", "Tomcat-Integration");
        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(views);
        assertEquals(2, softwareProjectIds.size());
        assertTrue(softwareProjectIds.contains(dev));
        assertTrue(softwareProjectIds.contains(integration));
    }
}
