package net.awired.visuwall.plugin.sonar;

import java.io.StringReader;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.Unmarshaller;

import net.awired.visuwall.plugin.sonar.resource.Properties;
import net.awired.visuwall.plugin.sonar.resource.Property;

import org.xml.sax.InputSource;

import com.google.common.base.Preconditions;

public class SonarVersionExtractor {

	private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

	private Properties properties;

	public SonarVersionExtractor(String content) {
		Preconditions.checkNotNull(content, "content is mandatory");
		InputSource is = new InputSource();
		is.setCharacterStream(new StringReader(content));
		properties = loadProperties(is);
	}

	public String version() {
		for (Property property : properties.getProperties()) {
			if (property.isKey(SONAR_CORE_VERSION_KEY)) {
				return property.getValue();
			}
		}
		return "unknown";
	}

	private Properties loadProperties(InputSource is) {
		try {
			JAXBContext newInstance = JAXBContext.newInstance(Properties.class);
			Unmarshaller unmarshaller = newInstance.createUnmarshaller();
			return (Properties) unmarshaller.unmarshal(is);
		} catch (Exception t) {
			throw new RuntimeException(t);
		}
	}
}
