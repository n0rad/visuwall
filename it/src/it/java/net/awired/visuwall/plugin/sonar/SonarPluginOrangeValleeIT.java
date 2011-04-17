package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.ProjectId;
import net.awired.visuwall.api.domain.quality.QualityResult;
import net.awired.visuwall.plugin.sonar.SonarPlugin;

import org.junit.BeforeClass;
import org.junit.Ignore;
import org.junit.Test;

@Ignore
public class SonarPluginOrangeValleeIT {

    private static SonarPlugin sonarPlugin;

    @BeforeClass
    public static void init() {
        sonarPlugin = new SonarPlugin();
        sonarPlugin.setUrl("http://10.2.40.60/lifeisbetteron/sonar");
        sonarPlugin.init();
    }

    @Test
    public void should_return_valid_lines_of_code_of_multimodule_project() {
        String metrics = "ncloc";
        ProjectId projectId = new ProjectId();
        projectId.setArtifactId("com.orangevallee.on.server.wme:wme");

        QualityResult qualityResult = sonarPlugin.populateQuality(projectId, metrics);
        assertEquals(20674, qualityResult.getMeasure("ncloc").getValue().doubleValue(), 0);
    }
}
