package net.awired.visuwall.plugin.teamcity;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;

public class TeamCityVersionExtractorTest {

	@Test
	public void should_extract_version_in_teamcity_page() throws IOException {
		Class<? extends TeamCityVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("teamcity_version_page.xml");
		byte[] data = ByteStreams.toByteArray(stream);
		TeamCityVersionExtractor tve = new TeamCityVersionExtractor(new String(data));

		String version = tve.version();
		assertEquals("6.5", version);
	}

}
