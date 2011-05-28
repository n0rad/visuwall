package net.awired.visuwall.plugin.sonar;

import org.junit.Test;


public class SonarPluginTest {

	@Test(expected = NullPointerException.class)
	public void should_throw_exception_when_passing_null_to_is_sonar_instance() {
		new SonarPlugin().isManageable(null);
	}
	
}
