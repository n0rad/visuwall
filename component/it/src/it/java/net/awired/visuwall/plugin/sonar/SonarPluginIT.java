package net.awired.visuwall.plugin.sonar;

import static net.awired.visuwall.IntegrationTestData.SONAR_URL;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class SonarPluginIT {

	@Test
	public void should_recognize_sonar_instance_with_valid_url() throws MalformedURLException {
		SonarPlugin sonarPlugin = new SonarPlugin();
		boolean isSonarInstance = sonarPlugin.isManageable(new URL(SONAR_URL));
		assertTrue(isSonarInstance);
	}
}
