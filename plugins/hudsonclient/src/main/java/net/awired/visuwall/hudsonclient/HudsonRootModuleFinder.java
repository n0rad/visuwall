/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package net.awired.visuwall.hudsonclient;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import net.awired.visuwall.hudsonclient.builder.HudsonUrlBuilder;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class HudsonRootModuleFinder {

	private HudsonUrlBuilder hudsonUrlBuilder;

	public HudsonRootModuleFinder(HudsonUrlBuilder hudsonUrlBuilder) {
		this.hudsonUrlBuilder = hudsonUrlBuilder;
	}

	public String findArtifactId(String jobName) throws ArtifactIdNotFoundException {
		String pomUrl = hudsonUrlBuilder.getPomUrl(jobName);

		String logMessage = "Can't find artifactId, job : " + jobName + " at url :'" + pomUrl + "'";
		try {
			URL url = new URL(pomUrl);
			Document doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().parse(url.openStream());
			String groupId = findFirstChildValue(doc, "groupId");
			String artifactId = findFirstChildValue(doc, "artifactId");

			if (groupId == null || artifactId == null) {
				throw new ArtifactIdNotFoundException(logMessage);
			}

			return groupId + ":" + artifactId;
		} catch (MalformedURLException e) {
			throw new ArtifactIdNotFoundException(logMessage, e);
		} catch (IOException e) {
			throw new ArtifactIdNotFoundException(logMessage, e);
		} catch (SAXException e) {
			throw new ArtifactIdNotFoundException(logMessage, e);
		} catch (ParserConfigurationException e) {
			throw new ArtifactIdNotFoundException(logMessage, e);
		}
	}

	private String findFirstChildValue(Document doc, String tagName) {
		NodeList artifactIds = doc.getElementsByTagName(tagName);
		for (int i = 0; i < artifactIds.getLength(); i++) {
			Node artifactId = artifactIds.item(i);
			String parentName = artifactId.getParentNode().getNodeName();
			if ("project".equals(parentName)) {
				return artifactId.getTextContent();
			}
		}
		return null;
	}

}
