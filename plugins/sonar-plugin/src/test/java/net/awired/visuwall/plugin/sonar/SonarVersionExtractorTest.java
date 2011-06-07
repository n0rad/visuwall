package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;

public class SonarVersionExtractorTest {

	@Test
	public void should_extract_version_in_jenkins_page() throws IOException {
		Class<? extends SonarVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("sonar_version_page.xml");
		byte[] data = ByteStreams.toByteArray(stream);
		SonarVersionExtractor sve = new SonarVersionExtractor(new String(data));

		String version = sve.version();
		assertEquals("2.8", version);
	}
}
