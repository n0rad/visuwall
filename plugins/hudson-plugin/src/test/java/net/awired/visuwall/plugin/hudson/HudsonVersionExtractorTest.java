package net.awired.visuwall.plugin.hudson;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;

public class HudsonVersionExtractorTest {

	@Test
	public void should_extract_version_in_jenkins_page() throws IOException {
		Class<? extends HudsonVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("hudson_version_page.html");
		byte[] data = ByteStreams.toByteArray(stream);
		HudsonVersionExtractor hve = new HudsonVersionExtractor(new String(data));

		String version = hve.version();
		assertEquals("1.396", version);
	}
}
