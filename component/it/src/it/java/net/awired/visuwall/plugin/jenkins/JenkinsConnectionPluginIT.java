package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.IntegrationTestData.JENKINS_URL;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import net.awired.visuwall.plugin.sonar.SonarConnectionPlugin;

import org.junit.Test;

public class JenkinsConnectionPluginIT {

	@Test
	public void should_recognize_sonar_instance_with_valid_url() throws MalformedURLException {
		SonarConnectionPlugin sonarConnectionPlugin = new SonarConnectionPlugin();
		boolean isSonarInstance = sonarConnectionPlugin.isSonarInstance(new URL(JENKINS_URL));
		assertTrue(isSonarInstance);
	}
}
