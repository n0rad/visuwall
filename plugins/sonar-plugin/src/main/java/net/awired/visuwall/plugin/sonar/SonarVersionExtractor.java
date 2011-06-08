package net.awired.visuwall.plugin.sonar;

import net.awired.visuwall.plugin.sonar.resource.Properties;
import net.awired.visuwall.plugin.sonar.resource.Property;

import com.google.common.base.Preconditions;

public class SonarVersionExtractor {

	private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";

	private Properties properties;

	public SonarVersionExtractor(Properties properties) {
		Preconditions.checkNotNull(properties, "properties is mandatory");
		this.properties = properties;
	}

	public String version() {
		for (Property property : properties.getProperties()) {
			if (property.isKey(SONAR_CORE_VERSION_KEY)) {
				return property.getValue();
			}
		}
		return "unknown";
	}

}
