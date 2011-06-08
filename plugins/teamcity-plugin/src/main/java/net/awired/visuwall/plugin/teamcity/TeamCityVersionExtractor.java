package net.awired.visuwall.plugin.teamcity;

import java.io.IOException;
import java.io.StringReader;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import com.google.common.base.Preconditions;

public class TeamCityVersionExtractor {

	private Document document;

	public TeamCityVersionExtractor(String content) {
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
		NodeList servers = document.getElementsByTagName("server");
		if (servers.getLength() == 1) {
			Node serverNode = servers.item(0);
			NamedNodeMap attributes = serverNode.getAttributes();
			Node versionMajorAttribute = attributes.getNamedItem("versionMajor");
			Node versionMinorAttribute = attributes.getNamedItem("versionMinor");
			String majorVersion = versionMajorAttribute.getNodeValue();
			String minorVersion = versionMinorAttribute.getNodeValue();
			return majorVersion + "." + minorVersion;
		}
		return "unknown";
	}

}
