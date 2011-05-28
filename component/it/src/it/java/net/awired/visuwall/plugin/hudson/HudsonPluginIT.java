package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertTrue;

import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;

public class HudsonPluginIT {
	@Test
	public void should_recognize_hudson_instance_with_valid_url() throws MalformedURLException {
		HudsonPlugin hudsonPlugin = new HudsonPlugin();
		boolean isHudsonInstance = hudsonPlugin.isManageable(new URL("http://fluxx.fr.cr:8080/hudson"));
		assertTrue(isHudsonInstance);
	}
}
