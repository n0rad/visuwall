package net.awired.visuwall.plugin.sonar;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

public class SonarVersionExtractor {

	private static final String SONAR_CORE_VERSION_KEY = "sonar.core.version";
	private final Document document;

	public SonarVersionExtractor(String content) {
		try {
			Preconditions.checkNotNull(content, "content is mandatory");
			DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
			DocumentBuilder documentBuilder = builderFactory.newDocumentBuilder();
			InputSource is = new InputSource();
			is.setCharacterStream(new StringReader(content));
			document = documentBuilder.parse(is);
		} catch (ParserConfigurationException e) {
			throw new RuntimeException(e);
		} catch (SAXException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String version() {
		Node root = document.getFirstChild();
		NodeList properties = root.getChildNodes();
		for (int i = 0; i < properties.getLength(); i++) {
			Node property = properties.item(i);
			String value = property.getTextContent();
			if (value.contains(SONAR_CORE_VERSION_KEY)) {
				String version = value.replace(SONAR_CORE_VERSION_KEY, "");
				return version.trim();
			}
		}
		return "unknown";
	}
}
