package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;
import net.awired.visuwall.api.domain.PluginInfo;
import net.awired.visuwall.api.exception.IncompatibleSoftwareException;

import org.junit.Test;

public class TeamCityPluginTest {

    @Test(expected = NullPointerException.class)
	public void should_thrown_an_exception_when_passing_null_to_is_jenkins_instance()
	        throws IncompatibleSoftwareException {
		new TeamCityPlugin().getSoftwareInfo(null);
    }

	@Test
	public void should_return_plugin_info() {
		PluginInfo expectedInfo = new PluginInfo();
		expectedInfo.setClassName(PluginInfo.class.getName());
		expectedInfo.setName("TeamCity plugin");
		expectedInfo.setVersion(1.0f);

		PluginInfo info = new TeamCityPlugin().getInfo();

		assertEquals(expectedInfo, info);
	}

}
