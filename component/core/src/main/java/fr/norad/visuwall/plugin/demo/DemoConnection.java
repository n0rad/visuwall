package fr.norad.visuwall.plugin.demo;

import static fr.norad.visuwall.api.domain.BuildState.FAILURE;
import static fr.norad.visuwall.api.domain.BuildState.SUCCESS;
import static fr.norad.visuwall.api.domain.BuildState.UNKNOWN;
import static fr.norad.visuwall.api.domain.BuildState.UNSTABLE;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.earth;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.mars;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.mercury;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.moon;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.neptune;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.pluto;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.saturn;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.uranus;
import static fr.norad.visuwall.plugin.demo.SoftwareProjectIds.venus;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import org.joda.time.DateTime;
import fr.norad.visuwall.api.domain.BuildState;
import fr.norad.visuwall.api.domain.BuildTime;
import fr.norad.visuwall.api.domain.Commiter;
import fr.norad.visuwall.api.domain.ProjectKey;
import fr.norad.visuwall.api.domain.SoftwareProjectId;
import fr.norad.visuwall.api.domain.TestResult;
import fr.norad.visuwall.api.domain.quality.QualityMeasure;
import fr.norad.visuwall.api.domain.quality.QualityMetric;
import fr.norad.visuwall.api.domain.quality.QualityResult;
import fr.norad.visuwall.api.exception.BuildIdNotFoundException;
import fr.norad.visuwall.api.exception.BuildNotFoundException;
import fr.norad.visuwall.api.exception.ConnectionException;
import fr.norad.visuwall.api.exception.MavenIdNotFoundException;
import fr.norad.visuwall.api.exception.ProjectNotFoundException;
import fr.norad.visuwall.api.exception.ViewNotFoundException;
import fr.norad.visuwall.api.plugin.capability.BuildCapability;
import fr.norad.visuwall.api.plugin.capability.MetricCapability;
import fr.norad.visuwall.api.plugin.capability.TestCapability;
import fr.norad.visuwall.api.plugin.capability.ViewCapability;

public class DemoConnection implements BuildCapability, TestCapability, ViewCapability, MetricCapability {

    private boolean connected;

    private Map<SoftwareProjectId, String> softwareProjectIds = new HashMap<SoftwareProjectId, String>();
    private Map<SoftwareProjectId, BuildState> buildStates = new HashMap<SoftwareProjectId, BuildState>();
    private Map<SoftwareProjectId, TestResult> unitTestResults = new HashMap<SoftwareProjectId, TestResult>();
    private Map<SoftwareProjectId, TestResult> integrationTestResults = new HashMap<SoftwareProjectId, TestResult>();
    private Map<SoftwareProjectId, QualityResult> qualityResults = new HashMap<SoftwareProjectId, QualityResult>();

    private List<String> views = new ArrayList<String>();

    private Integer marsBuildId = 1;

    private List<String> marsBuildIds = new ArrayList<String>();

    private ChangeStateProject marsProj = new ChangeStateProject();

    public DemoConnection() {
        softwareProjectIds.put(moon, "Moon");
        softwareProjectIds.put(earth, "Earth");
        softwareProjectIds.put(mars, "Mars");
        softwareProjectIds.put(pluto, "Pluto");
        softwareProjectIds.put(neptune, "Neptune");
        softwareProjectIds.put(uranus, "Uranus");
        softwareProjectIds.put(saturn, "Saturn");
        softwareProjectIds.put(mercury, "Mercury");
        softwareProjectIds.put(venus, "Venus");

        buildStates.put(mars, FAILURE);
        buildStates.put(pluto, UNKNOWN);
        buildStates.put(uranus, SUCCESS);
        buildStates.put(neptune, SUCCESS);
        buildStates.put(saturn, UNSTABLE);
        buildStates.put(venus, UNSTABLE);
        buildStates.put(moon, SUCCESS);
        buildStates.put(earth, SUCCESS);
        buildStates.put(mercury, SUCCESS);

        views.add("Telluriques");
        views.add("Gazeuses");
        views.add("Other");

        TestResult saturnTestResults = createTestResult(78, 120, 10, 20);
        TestResult venusTestResults = createTestResult(25, 457, 3, 16);
        TestResult neptuneTestResults = createTestResult(90, 872, 0, 0);
        TestResult mercuryTestResults = createTestResult(78, 439, 0, 0);

        unitTestResults.put(saturn, saturnTestResults);
        unitTestResults.put(neptune, neptuneTestResults);
        unitTestResults.put(mercury, mercuryTestResults);
        unitTestResults.put(venus, venusTestResults);

        TestResult neptuneIntegrationTestResults = createTestResult(78, 163, 0, 0);
        TestResult mercuryIntegrationTestResults = createTestResult(89, 236, 0, 0);
        TestResult venusIntegrationTestResults = createTestResult(49, 178, 4, 2);

        integrationTestResults.put(neptune, neptuneIntegrationTestResults);
        integrationTestResults.put(mercury, mercuryIntegrationTestResults);
        integrationTestResults.put(venus, venusIntegrationTestResults);

        QualityMeasure uranusCoverageMeasure = createQualityMeasure("coverage", "Coverage", "76.5 %", 76.5);
        QualityMeasure uranusLocMeasure = createQualityMeasure("ncloc", "Lines of code", "78.001", 78001D);
        QualityMeasure uranusViolationsMeasure = createQualityMeasure("violations_density", "Violations", "32", 32D);
        QualityResult uranusQualityResult = new QualityResult();
        uranusQualityResult.add("coverage", uranusCoverageMeasure);
        uranusQualityResult.add("ncloc", uranusLocMeasure);
        uranusQualityResult.add("violations_density", uranusViolationsMeasure);

        QualityResult mercuryQualityResult = new QualityResult();
        QualityMeasure mercuryLocMeasure = createQualityMeasure("ncloc", "Lines of code", "121.988", 121988D);
        mercuryQualityResult.add("ncloc", mercuryLocMeasure);

        qualityResults.put(uranus, uranusQualityResult);
        qualityResults.put(mercury, mercuryQualityResult);

        marsBuildIds.add("1");
    }

    private QualityMeasure createQualityMeasure(String key, String name, String formattedValue, double value) {
        QualityMeasure coverageMeasure = new QualityMeasure();
        coverageMeasure.setKey(key);
        coverageMeasure.setName(name);
        coverageMeasure.setFormattedValue(formattedValue);
        coverageMeasure.setValue(value);
        return coverageMeasure;
    }

    private TestResult createTestResult(int coverage, int passCount, int failCount, int skipCount) {
        TestResult saturnTestResults = new TestResult();
        saturnTestResults.setCoverage(coverage);
        saturnTestResults.setFailCount(failCount);
        saturnTestResults.setSkipCount(skipCount);
        saturnTestResults.setPassCount(passCount);
        return saturnTestResults;
    }

    @Override
    public void connect(String url, String login, String password) throws ConnectionException {
        connected = true;
    }

    @Override
    public void close() {
        connected = false;
    }

    @Override
    public boolean isClosed() {
        return !connected;
    }

    @Override
    public Map<SoftwareProjectId, String> listSoftwareProjectIds() {
        return softwareProjectIds;
    }

    @Override
    public String getMavenId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            MavenIdNotFoundException {
        if (softwareProjectId.getProjectId() != null) {
            return "net.awired.visuwall.plugin.demo:" + softwareProjectId.getProjectId();
        }
        throw new MavenIdNotFoundException("Cannot find maven id for " + softwareProjectId);
    }

    @Override
    public String getDescription(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return "";
    }

    @Override
    public String getName(SoftwareProjectId projectId) throws ProjectNotFoundException {
        String name = projectId.getProjectId();
        String firstLetter = "" + name.charAt(0);
        return firstLetter.toUpperCase() + name.substring(1);
    }

    @Override
    public SoftwareProjectId identify(ProjectKey projectKey) throws ProjectNotFoundException {
        String projectName = projectKey.getName();
        SoftwareProjectId softwareProjectId = getByName(projectName);
        if (softwareProjectId == null) {
            throw new ProjectNotFoundException("Cannot find project for " + projectKey);
        }
        return softwareProjectId;
    }

    private SoftwareProjectId getByName(String projectName) {
        Set<Entry<SoftwareProjectId, String>> entries = softwareProjectIds.entrySet();
        for (Entry<SoftwareProjectId, String> entry : entries) {
            if (entry.getValue().equals(projectName)) {
                return entry.getKey();
            }
        }
        return null;
    }

    @Override
    public boolean isProjectDisabled(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        return false;
    }

    @Override
    public Map<String, List<QualityMetric>> getMetricsByCategory() {
        return new HashMap<String, List<QualityMetric>>();
    }

    @Override
    public QualityResult analyzeQuality(SoftwareProjectId projectId, String... metrics) {
        return qualityResults.get(projectId);
    }

    @Override
    public List<SoftwareProjectId> findSoftwareProjectIdsByViews(List<String> views) {
        List<SoftwareProjectId> softwareProjectIds = new ArrayList<SoftwareProjectId>();
        if (views.contains("Telluriques")) {
            softwareProjectIds.add(earth);
        }
        return softwareProjectIds;
    }

    @Override
    public List<String> findViews() {
        return views;
    }

    @Override
    public List<String> findProjectNamesByView(String viewName) throws ViewNotFoundException {
        List<String> projectNames = new ArrayList<String>();
        if ("Telluriques".equals(viewName)) {
            projectNames.add("Earth");
        }
        return projectNames;
    }

    @Override
    public TestResult analyzeUnitTests(SoftwareProjectId projectId) {
        TestResult testResult = unitTestResults.get(projectId);
        if (testResult == null) {
            testResult = new TestResult();
        }
        return testResult;
    }

    @Override
    public TestResult analyzeIntegrationTests(SoftwareProjectId projectId) {
        TestResult testResult = integrationTestResults.get(projectId);
        if (testResult == null) {
            testResult = new TestResult();
        }
        return testResult;
    }

    @Override
    public List<Commiter> getBuildCommiters(SoftwareProjectId softwareProjectId, String buildId)
            throws BuildNotFoundException, ProjectNotFoundException {
        List<Commiter> commiters = new ArrayList<Commiter>();
        if (softwareProjectId.equals(mars)) {
            return marsProj.getCommiters(buildId);
        }
        return commiters;
    }

    @Override
    public BuildTime getBuildTime(SoftwareProjectId softwareProjectId, String buildId) throws BuildNotFoundException,
            ProjectNotFoundException {
        if (softwareProjectId.equals(mars)) {
            return marsProj.getBuildTime(buildId);
        }
        BuildTime buildTime = new BuildTime();
        int milisDuration = randomDuration();
        buildTime.setDuration(milisDuration);
        Date startDate = randomPastDate();
        buildTime.setStartTime(startDate);
        return buildTime;
    }

    private Date randomPastDate() {
        int minutesAgo = (int) (Math.random() * 50);
        Date startDate = new DateTime().minusHours(minutesAgo).toDate();
        return startDate;
    }

    private int randomDuration() {
        return (int) (Math.random() * 5000) * 60;
    }

    @Override
    public List<String> getBuildIds(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException {
        if (mars.equals(softwareProjectId)) {
            return marsProj.getbuildIds();
        }
        List<String> buildIds = new ArrayList<String>();
        buildIds.add("1");
        return buildIds;
    }

    @Override
    public BuildState getBuildState(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        BuildState buildState = buildStates.get(projectId);
        if (buildState == null) {
            throw new ProjectNotFoundException("Cannot find project for " + projectId);
        }
        if (mars.equals(projectId)) {
            return marsProj.getBuildState(buildId);
        }
        return buildState;
    }

    @Override
    public Date getEstimatedFinishTime(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        if (projectId.equals(mars)) {
            return marsProj.estimatedFinishTime();
        }
        if (projectId.equals(moon)) {
            Date date = new DateTime().plusHours(8).toDate();
            return date;
        }
        return new Date();
    }

    @Override
    public boolean isBuilding(SoftwareProjectId projectId, String buildId) throws ProjectNotFoundException,
            BuildNotFoundException {
        if (projectId.equals(mars)) {
            return marsProj.isBuilding();
        }
        if (projectId.equals(moon)) {
            return true;
        }
        return false;
    }

    @Override
    public String getLastBuildId(SoftwareProjectId softwareProjectId) throws ProjectNotFoundException,
            BuildIdNotFoundException {
        String lastBuildId = "1";
        if (softwareProjectId.equals(mars)) {
            return marsProj.getLastBuildId();
        }
        return lastBuildId;
    }

    @Override
    public String toString() {
        return "Demo Connection";
    }

}
