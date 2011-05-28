package net.awired.visuwall.plugin.jenkins;

import org.junit.Test;


public class JenkinsPluginTest {

	@Test(expected = NullPointerException.class)
	public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance() {
		new JenkinsPlugin().isManageable(null);
	}
}
