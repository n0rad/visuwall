package net.awired.visuwall.plugin.jenkins;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import org.junit.Test;

import com.google.common.io.ByteStreams;

public class JenkinsVersionExtractorTest {

	@Test
	public void should_extract_version() {
		String content = "6 juin 2011 21:50:35</span><a href=\"http://jenkins-ci.org/\">Jenkins ver. 1.407</a></td></tr></table></body></html>";
		JenkinsVersionExtractor jve = new JenkinsVersionExtractor(content);
		String version = jve.version();
		assertEquals("1.407", version);
	}

	@Test
	public void should_extract_version_in_jenkins_page() throws IOException {
		Class<? extends JenkinsVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("jenkins_version_page.html");
		byte[] data = ByteStreams.toByteArray(stream);
		JenkinsVersionExtractor jve = new JenkinsVersionExtractor(new String(data));

		String version = jve.version();
		assertEquals("1.407", version);
	}

}
