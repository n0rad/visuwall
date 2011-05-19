package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.IntegrationTestData.JENKINS_URL;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class JenkinsConnectionPluginIT {

	@Test
	public void should_recognize_jenkins_instance_with_valid_url() throws MalformedURLException {
		JenkinsConnectionPlugin jenkinsConnectionPlugin = new JenkinsConnectionPlugin();
		boolean isJenkinsInstance = jenkinsConnectionPlugin.isJenkinsInstance(new URL(JENKINS_URL));
		assertTrue(isJenkinsInstance);
	}
}
