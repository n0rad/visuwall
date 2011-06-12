package net.awired.visuwall.api.plugin.capability;

import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.TestResult;
import net.awired.visuwall.api.plugin.ConnectionPlugin;

public interface TestsPlugin extends ConnectionPlugin {
    /**
     * Generate the unit tests reporting
     * 
     * @param projectId
     * @return
     */
    TestResult analyzeUnitTests(ProjectId projectId);

    /**
     * Generate the integration tests reporting
     * 
     * @param projectId
     * @return
     */
    TestResult analyzeIntegrationTests(ProjectId projectId);

}
