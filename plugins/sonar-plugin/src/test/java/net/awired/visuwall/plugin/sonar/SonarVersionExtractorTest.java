package net.awired.visuwall.plugin.sonar;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.io.InputStream;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.plugin.sonar.resource.Properties;

import org.junit.Test;

public class SonarVersionExtractorTest {

	@Test
	public void should_extract_version_in_jenkins_page() throws IOException {
		Class<? extends SonarVersionExtractorTest> clazz = this.getClass();
		ClassLoader classLoader = clazz.getClassLoader();
		InputStream stream = classLoader.getResourceAsStream("sonar_version_page.xml");
		SonarVersionExtractor sve = new SonarVersionExtractor(loadProperties(stream));

		String version = sve.version();
		assertEquals("2.8", version);
	}

	private Properties loadProperties(InputStream is) {
		try {
			JAXBContext newInstance = JAXBContext.newInstance(Properties.class);
			Unmarshaller unmarshaller = newInstance.createUnmarshaller();
			return (Properties) unmarshaller.unmarshal(is);
		} catch (Exception t) {
			throw new RuntimeException(t);
		}
	}
}
