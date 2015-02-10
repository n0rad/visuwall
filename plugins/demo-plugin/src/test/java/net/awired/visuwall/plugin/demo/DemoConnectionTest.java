package net.awired.visuwall.plugin.demo;

import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.earth;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.mars;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.moon;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.neptune;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.pluto;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.saturn;
import static net.awired.visuwall.plugin.demo.SoftwareProjectIds.uranus;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;
import net.awired.visuwall.api.domain.BuildTime;
import net.awired.visuwall.api.domain.Commiter;
import net.awired.visuwall.api.domain.ProjectKey;
import net.awired.visuwall.api.domain.SoftwareProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.domain.quality.QualityMeasure;
import net.awired.visuwall.api.domain.quality.QualityMetric;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.api.exception.BuildIdNotFoundException;
import net.awired.visuwall.api.exception.BuildNotFoundException;
import net.awired.visuwall.api.exception.ConnectionException;
import net.awired.visuwall.api.exception.MavenIdNotFoundException;
import net.awired.visuwall.api.exception.ProjectNotFoundException;
import net.awired.visuwall.api.exception.ViewNotFoundException;
import org.junit.Before;
import org.junit.Test;

public class DemoConnectionTest {

    DemoConnection connection;

    @Before
    public void init() {
        connection = new DemoConnection();
    }

    @Test
    public void should_connect_with_everything() throws ConnectionException {
        connection.connect(null, null, null);
    }

    @Test
    public void should_close() throws ConnectionException {
        connection.close();
    }

    @Test
    public void should_be_closed_after_close() throws ConnectionException {
        connection.connect(null, null, null);
        assertFalse(connection.isClosed());
        connection.close();
        assertTrue(connection.isClosed());
    }

    @Test
    public void should_get_all_projects() {
        Map<SoftwareProjectId, String> softwareProjectIds = connection.listSoftwareProjectIds();
        Collection<String> projectNames = softwareProjectIds.values();
        assertTrue(projectNames.contains("Earth"));
    }

    @Test
    public void should_get_maven_id_for_earth() throws ProjectNotFoundException, MavenIdNotFoundException {
        String mavenId = connection.getMavenId(earth);
        assertEquals("net.awired.visuwall.plugin.demo:earth", mavenId);
    }

    @Test
    public void should_get_empty_description_for_everything() throws ProjectNotFoundException,
            MavenIdNotFoundException {
        String description = connection.getDescription(null);
        assertTrue(description.isEmpty());
    }

    @Test
    public void should_get_name_for_earth() throws ProjectNotFoundException {
        String name = connection.getName(earth);
        assertEquals("Earth", name);
    }

    @Test
    public void should_get_name_for_pluto() throws ProjectNotFoundException {
        String name = connection.getName(pluto);
        assertEquals("Pluto", name);
    }

    @Test
    public void should_identify_earth() throws ProjectNotFoundException {
        ProjectKey projectKey = new ProjectKey();
        projectKey.setName("Earth");
        SoftwareProjectId softwareProjectId = connection.identify(projectKey);
        assertEquals(earth, softwareProjectId);
    }

    @Test
    public void should_projects_are_not_disabled() throws ProjectNotFoundException {
        boolean projectDisabled = connection.isProjectDisabled(earth);
        assertFalse(projectDisabled);
    }

    @Test
    public void moon_should_be_building() throws ProjectNotFoundException, BuildNotFoundException {
        boolean building = connection.isBuilding(moon, "");
        assertTrue(building);
    }

    @Test
    public void earth_should_not_be_building() throws ProjectNotFoundException, BuildNotFoundException {
        boolean building = connection.isBuilding(earth, "");
        assertFalse(building);
    }

    @Test
    public void last_build_id_is_always_0() throws ProjectNotFoundException, BuildIdNotFoundException {
        String lastBuildId = connection.getLastBuildId(earth);
        assertEquals("1", lastBuildId);
    }

    @Test
    public void moon_is_building_for_2_minutes() throws ProjectNotFoundException, BuildNotFoundException {
        Date estimatedFinishTime = connection.getEstimatedFinishTime(moon, "");
        assertNotNull(estimatedFinishTime);
    }

    @Test
    public void should_get_build_time_for_earth() throws BuildNotFoundException, ProjectNotFoundException {
        BuildTime earthBuildTime = connection.getBuildTime(earth, "");
        assertTrue(earthBuildTime.getDuration() > 1000);
        assertNotNull(earthBuildTime.getStartTime());
    }

    @Test
    public void should_not_have_commiters_on_earth() throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = connection.getBuildCommiters(earth, "");
        assertTrue(commiters.isEmpty());
    }

    @Test
    public void should_have_three_views() {
        List<String> views = connection.findViews();
        assertEquals(3, views.size());
        assertEquals("Telluriques", views.get(0));
        assertEquals("Gazeuses", views.get(1));
        assertEquals("Other", views.get(2));
    }

    @Test
    public void should_get_earth_on_view_1() throws ViewNotFoundException {
        List<String> projectNames = connection.findProjectNamesByView("Telluriques");
        assertTrue(projectNames.contains("Earth"));
    }

    @Test
    public void should_get_earth_project_id_on_view_1() {
        List<String> views = new ArrayList<String>();
        views.add("Telluriques");
        List<SoftwareProjectId> softwareProjectIds = connection.findSoftwareProjectIdsByViews(views);
        assertTrue(softwareProjectIds.contains(earth));
    }

    @Test
    public void saturn_should_have_a_lot_of_tests_in_different_states() {
        TestResult testResult = connection.analyzeUnitTests(saturn);
        assertEquals(78, testResult.getCoverage(), 0);
        assertEquals(10, testResult.getFailCount());
        assertEquals(20, testResult.getSkipCount());
        assertEquals(120, testResult.getPassCount());
    }

    @Test
    public void neptune_should_have_integration_tests_and_unit_tests_all_pass() {
        TestResult unitTestResult = connection.analyzeUnitTests(neptune);
        assertEquals(90, unitTestResult.getCoverage(), 0);
        assertEquals(872, unitTestResult.getPassCount());

        TestResult integrationTestResult = connection.analyzeIntegrationTests(neptune);
        assertEquals(78, integrationTestResult.getCoverage(), 0);
        assertEquals(163, integrationTestResult.getPassCount());
    }

    @Test
    public void uranus_should_have_quality_metrics() {
        String[] metrics = new String[] { "coverage", "ncloc", "violations_density", "it_coverage" };

        QualityResult qualityResult = connection.analyzeQuality(uranus, metrics);
        QualityMeasure coverageMeasure = new QualityMeasure();
        coverageMeasure.setKey("coverage");
        coverageMeasure.setName("Coverage");
        coverageMeasure.setFormattedValue("76.5 %");
        coverageMeasure.setValue(76.5);
        assertEquals(coverageMeasure, qualityResult.getMeasure("coverage"));
    }

    @Test
    public void should_get_an_empty_map_for_metrics_by_categories() {
        Map<String, List<QualityMetric>> metricsByCategory = connection.getMetricsByCategory();
        assertTrue(metricsByCategory.isEmpty());
    }

    @Test
    public void should_have_one_build_id_for_every_project() throws ProjectNotFoundException {
        assertEquals(1, connection.getBuildIds(uranus).size());
        assertEquals(1, connection.getBuildIds(earth).size());
        assertEquals(2, connection.getBuildIds(mars).size());
        assertEquals(1, connection.getBuildIds(moon).size());
        assertEquals(1, connection.getBuildIds(pluto).size());
        assertEquals(1, connection.getBuildIds(neptune).size());
    }

    @Test
    public void should_get_empty_unit_test_result_if_there_is_no_data() {
        TestResult testResult = connection.analyzeIntegrationTests(earth);
        assertEquals(0, testResult.getCoverage(), 0);
        assertEquals(0, testResult.getFailCount());
        assertEquals(0, testResult.getSkipCount());
        assertEquals(0, testResult.getPassCount());
    }

}
