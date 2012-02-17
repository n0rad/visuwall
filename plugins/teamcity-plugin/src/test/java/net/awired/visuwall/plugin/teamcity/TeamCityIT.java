package net.awired.visuwall.plugin.teamcity;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.awired.visuwall.api.domain.BuildState;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.exception.ViewNotFoundException;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

public class TeamCityIT {

    static TeamCityConnection connection;

    @BeforeClass
    public static void init() {
        connection = new TeamCityConnection();
        connection.connect("http://teamcity.jetbrains.com", "guest", "");
    }

    @Test
    public void should_get_view_names() {
        List<String> views = connection.findViews();
        assertTrue(views.contains("Apache Ant"));
        assertTrue(views.contains("Apache Ivy"));
    }

    @Test
    public void should_get_project_names_by_view() throws ViewNotFoundException {
        List<String> projectNames = connection.findProjectNamesByView("Apache Ant");
        assertTrue(projectNames.contains("Checkstyle"));
        assertTrue(projectNames.contains("Distribution"));
    }

    @Test
    public void should_get_description() throws Exception {
        String expectedDescription = "Check the conformance against coding styleguide";
        String buildTypeId = "bt132";

        SoftwareProjectId softwareProjectId = new SoftwareProjectId(buildTypeId);
        String description = connection.getDescription(softwareProjectId);
        assertEquals(expectedDescription, description);
    }

    @Test
    public void should_get_software_project_id_by_view() throws ViewNotFoundException {
        List<String> viewNames = asList("Apache Ant");
        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(viewNames);
        SoftwareProjectId softwareProjectId = softwareProjectIds.get(0);
        assertEquals("bt132", softwareProjectId.getProjectId());
    }

    @Test
    public void should_get_build_ids() throws Exception {
        String buildTypeId = "bt132";

        SoftwareProjectId softwareProjectId = new SoftwareProjectId(buildTypeId);
        String lastBuildId = connection.getLastBuildId(softwareProjectId);

        List<String> buildIds = connection.getBuildIds(softwareProjectId);
        assertTrue(buildIds.contains(lastBuildId));
    }

    @Ignore
    @Test
    public void should_list_software_projects_ids() {

        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();

        Entry<SoftwareProjectId, String> next = softwareProjectIds.entrySet().iterator().next();
        SoftwareProjectId softwareProjectId = next.getKey();
        String value = next.getValue();
        assertEquals("Release Branch (Windows)", value);
        assertEquals("bt113", softwareProjectId.getProjectId());
    }

    @Test
    public void should_get_name() throws Exception {
        String expectedName = "Checkstyle";
        String buildTypeId = "bt132";

        SoftwareProjectId softwareProjectId = new SoftwareProjectId(buildTypeId);
        String description = connection.getName(softwareProjectId);
        assertEquals(expectedName, description);
    }

    @Test
    public void should_get_state() throws Exception {
        String buildTypeId = "bt132";

        SoftwareProjectId softwareProjectId = new SoftwareProjectId(buildTypeId);
        BuildState state = connection.getBuildState(softwareProjectId, connection.getLastBuildId(softwareProjectId));
        assertEquals(BuildState.SUCCESS, state);
    }
}
