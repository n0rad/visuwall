package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertFalse;
import java.util.List;
import org.junit.Test;

public class SonarConnectionIT {

    @Test
    public void should_find_project_names() {
        SonarConnection sonar = new SonarConnection();
        sonar.connect("http://nemo.sonarsource.org");
        List<String> projectNames = sonar.findProjectNames();
        assertFalse(projectNames.isEmpty());
    }

}
