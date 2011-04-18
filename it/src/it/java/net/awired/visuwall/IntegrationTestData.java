package net.awired.visuwall;

import net.awired.visuwall.core.domain.Software;
import net.awired.visuwall.core.domain.SoftwareAccess;
import net.awired.visuwall.plugin.hudson.HudsonPlugin;
import net.awired.visuwall.plugin.sonar.SonarPlugin;

public interface IntegrationTestData {

    String BAMBOO_URL = "http://bamboo.visuwall.awired.net";
    String HUDSON_URL = "http://ci.visuwall.awired.net";
    String HUDSON_ID = "HUDSON_ID";
    String SONAR_URL = "http://sonar.awired.net";
    String STRUTS_ARTIFACT_ID = "org.apache.struts:struts-core";

    SoftwareAccess HUDSON_ACCESS = new SoftwareAccess(new Software(HudsonPlugin.class.getName(), 1.0f), HUDSON_URL);
    SoftwareAccess SONAR_ACCESS = new SoftwareAccess(new Software(SonarPlugin.class.getName(), 1.0f), SONAR_URL);

}
