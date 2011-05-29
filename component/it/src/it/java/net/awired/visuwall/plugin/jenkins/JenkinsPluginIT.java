package net.awired.visuwall.plugin.jenkins;

import static net.awired.visuwall.IntegrationTestData.JENKINS_URL;
import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class JenkinsPluginIT {
	@Test
	public void should_recognize_jenkins_instance_with_valid_url() throws MalformedURLException {
		JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
		boolean isJenkinsInstance = jenkinsPlugin.isManageable(new URL(JENKINS_URL));
		assertTrue(isJenkinsInstance);
	}

	// @Ignore
	@Test
	public void should_recognize_jenkins_instance_with_https() throws MalformedURLException {
		JenkinsPlugin jenkinsPlugin = new JenkinsPlugin();
		boolean isJenkinsInstance = jenkinsPlugin.isManageable(new URL("https://builds.apache.org"));
		assertTrue(isJenkinsInstance);
	}

}
